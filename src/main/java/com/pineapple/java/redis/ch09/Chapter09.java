package com.pineapple.java.redis.ch09;

import org.javatuples.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ZParams;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class Chapter09 {
    private long USERS_PER_SHARD = (long) Math.pow(2, 20);
    private int SHARD_SIZE = 512;
    private long DAILY_EXPECTED = 1000000;
    private Map<String, Long> EXPECTED = new HashMap<String, Long>();

    static {
        StringUtil.STATES.put("CAN", "AB BC MB NB NL NS NT NU ON PE QC SK YT".split(" "));
        StringUtil.STATES.put("USA", (
                "AA AE AK AL AP AR AS AZ CA CO CT DC DE FL FM GA GU HI IA ID IL IN " +
                        "KS KY LA MA MD ME MH MI MN MO MP MS MT NC ND NE NH NJ NM NV NY OH " +
                        "OK OR PA PR PW RI SC SD TN TX UT VA VI VT WA WI WV WY").split(" "));
    }

    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00");

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final void main(String[] args) {
        new Chapter09().run();
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);
        conn.flushDB();

        ZipList.testLongZiplistPerformance(conn);
        Sharding.testShardKey(conn);
        Sharding.testShardedHash(conn);
        Sharding.testShardedSadd(conn);
        testUniqueVisitors(conn);
        testUserLocation(conn);
    }

    public void testUniqueVisitors(Jedis conn) {
        System.out.println("\n----- testUniqueVisitors -----");

        DAILY_EXPECTED = 10000;

        for (int i = 0; i < 179; i++) {
            countVisit(conn, UUID.randomUUID().toString());
        }
        assert "179".equals(conn.get("unique:" + ISO_FORMAT.format(new Date())));

        conn.flushDB();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        conn.set("unique:" + ISO_FORMAT.format(yesterday.getTime()), "1000");
        for (int i = 0; i < 183; i++) {
            countVisit(conn, UUID.randomUUID().toString());
        }
        assert "183".equals(conn.get("unique:" + ISO_FORMAT.format(new Date())));
    }

    public void testUserLocation(Jedis conn) {
        System.out.println("\n----- testUserLocation -----");

        int i = 0;
        for (String country : StringUtil.COUNTRIES) {
            if (StringUtil.STATES.containsKey(country)) {
                for (String state : StringUtil.STATES.get(country)) {
                    setLocation(conn, i, country, state);
                    i++;
                }
            } else {
                setLocation(conn, i, country, "");
                i++;
            }
        }

        Pair<Map<String, Long>, Map<String, Map<String, Long>>> _aggs = aggregateLocation(conn);

        long[] userIds = new long[i + 1];
        for (int j = 0; j <= i; j++) {
            userIds[j] = j;
        }
        Pair<Map<String, Long>, Map<String, Map<String, Long>>> aggs = aggregateLocationList(conn, userIds);

        assert _aggs.equals(aggs);

        Map<String, Long> countries = aggs.getValue0();
        Map<String, Map<String, Long>> states = aggs.getValue1();
        for (String country : aggs.getValue0().keySet()) {
            if (StringUtil.STATES.containsKey(country)) {
                assert StringUtil.STATES.get(country).length == countries.get(country);
                for (String state : StringUtil.STATES.get(country)) {
                    assert states.get(country).get(state) == 1;
                }
            } else {
                assert countries.get(country) == 1;
            }
        }
    }

    public void countVisit(Jedis conn, String sessionId) {
        Calendar today = Calendar.getInstance();
        String key = "unique:" + ISO_FORMAT.format(today.getTime());
        long expected = getExpected(conn, key, today);
        long id = Long.parseLong(sessionId.replace("-", "").substring(0, 15), 16);
        if (Sharding.shardSadd(conn, key, String.valueOf(id), expected, SHARD_SIZE) != 0) {
            conn.incr(key);
        }
    }

    public long getExpected(Jedis conn, String key, Calendar today) {
        if (!EXPECTED.containsKey(key)) {
            String exkey = key + ":expected";
            String expectedStr = conn.get(exkey);

            long expected = 0;
            if (expectedStr == null) {
                Calendar yesterday = (Calendar) today.clone();
                yesterday.add(Calendar.DATE, -1);
                expectedStr = conn.get("unique:" + ISO_FORMAT.format(yesterday.getTime()));
                expected = expectedStr != null ? Long.parseLong(expectedStr) : DAILY_EXPECTED;

                expected = (long) Math.pow(2, (long) (Math.ceil(Math.log(expected * 1.5) / Math.log(2))));
                if (conn.setnx(exkey, String.valueOf(expected)) == 0) {
                    expectedStr = conn.get(exkey);
                    expected = Integer.parseInt(expectedStr);
                }
            } else {
                expected = Long.parseLong(expectedStr);
            }

            EXPECTED.put(key, expected);
        }

        return EXPECTED.get(key);
    }


    public void setLocation(Jedis conn, long userId, String country, String state) {
        String code = StringUtil.getCode(country, state);

        long shardId = userId / USERS_PER_SHARD;
        int position = (int) (userId % USERS_PER_SHARD);
        int offset = position * 2;

        Pipeline pipe = conn.pipelined();
        pipe.setrange("location:" + shardId, offset, code);

        String tkey = UUID.randomUUID().toString();
        pipe.zadd(tkey, userId, "max");
        pipe.zunionstore(
                "location:max",
                new ZParams().aggregate(ZParams.Aggregate.MAX),
                tkey,
                "location:max");
        pipe.del(tkey);
        pipe.sync();
    }

    public Pair<Map<String, Long>, Map<String, Map<String, Long>>> aggregateLocation(Jedis conn) {
        Map<String, Long> countries = new HashMap<String, Long>();
        Map<String, Map<String, Long>> states = new HashMap<String, Map<String, Long>>();

        long maxId = conn.zscore("location:max", "max").longValue();
        long maxBlock = maxId;

        byte[] buffer = new byte[(int) Math.pow(2, 17)];
        for (int shardId = 0; shardId <= maxBlock; shardId++) {
            InputStream in = new RedisInputStream(conn, "location:" + shardId);
            try {
                int read = 0;
                while ((read = in.read(buffer, 0, buffer.length)) != -1) {
                    for (int offset = 0; offset < read - 1; offset += 2) {
                        String code = new String(buffer, offset, 2);
                        StringUtil.updateAggregates(countries, states, code);
                    }
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }

        return new Pair<Map<String, Long>, Map<String, Map<String, Long>>>(countries, states);
    }

    public Pair<Map<String, Long>, Map<String, Map<String, Long>>> aggregateLocationList(Jedis conn, long[] userIds) {
        Map<String, Long> countries = new HashMap<String, Long>();
        Map<String, Map<String, Long>> states = new HashMap<String, Map<String, Long>>();

        Pipeline pipe = conn.pipelined();
        for (int i = 0; i < userIds.length; i++) {
            long userId = userIds[i];
            long shardId = userId / USERS_PER_SHARD;
            int position = (int) (userId % USERS_PER_SHARD);
            int offset = position * 2;

            pipe.substr("location:" + shardId, offset, offset + 1);

            if ((i + 1) % 1000 == 0) {
                StringUtil.updateAggregates(countries, states, pipe.syncAndReturnAll());
            }
        }

        StringUtil.updateAggregates(countries, states, pipe.syncAndReturnAll());

        return new Pair<Map<String, Long>, Map<String, Map<String, Long>>>(countries, states);
    }

}
