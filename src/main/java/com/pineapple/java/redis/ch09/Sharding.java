package com.pineapple.java.redis.ch09;

import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import java.util.zip.CRC32;

public class Sharding {
    public static final String TEST = "test";

    @Test
    public static void testShardKey(Jedis conn) {
        System.out.println("\n----- testShardKey -----");

        String base = TEST;
        assert "test:0".equals(shardKey(base, "1", 2, 2));
        assert "test:1".equals(shardKey(base, "125", 1000, 100));

        for (int i = 0; i < 50; i++) {
            String key = shardKey(base, "hello:" + i, 1000, 100);
            String[] parts = key.split(":");
            assert Integer.parseInt(parts[parts.length - 1]) < 20;

            key = shardKey(base, String.valueOf(i), 1000, 100);
            parts = key.split(":");
            assert Integer.parseInt(parts[parts.length - 1]) < 10;
        }
    }

    public static void testShardedHash(Jedis conn) {
        System.out.println("\n----- testShardedHash -----");

        for (int i = 0; i < 50; i++) {
            String istr = String.valueOf(i);
            shardHset(conn, TEST, "keyname:" + i, istr, 1000, 100);
            assert istr.equals(shardHget(conn, TEST, "keyname:" + i, 1000, 100));
            shardHset(conn, "test2", istr, istr, 1000, 100);
            assert istr.equals(shardHget(conn, "test2", istr, 1000, 100));
        }
    }

    public static void testShardedSadd(Jedis conn) {
        System.out.println("\n----- testShardedSadd -----");

        for (int i = 0; i < 50; i++) {
            shardSadd(conn, "testx", String.valueOf(i), 50, 50);
        }
        assert conn.scard("testx:0") + conn.scard("testx:1") == 50;
    }

    public static Long shardHset(Jedis conn, String base, String key, String value, long totalElements, int shardSize) {
        String shard = shardKey(base, key, totalElements, shardSize);
        return conn.hset(shard, key, value);
    }

    public static String shardHget(Jedis conn, String base, String key, int totalElements, int shardSize) {
        String shard = shardKey(base, key, totalElements, shardSize);
        return conn.hget(shard, key);
    }

    public static Long shardSadd(Jedis conn, String base, String member, long totalElements, int shardSize) {
        String shard = shardKey(base, "x" + member, totalElements, shardSize);
        return conn.sadd(shard, member);
    }

    public static String shardKey(String base, String key, long totalElements, int shardSize) {
        long shardId = 0;
        if (StringUtil.isDigit(key)) {
            shardId = Integer.parseInt(key, 10) / shardSize;
        } else {
            CRC32 crc = new CRC32();
            crc.update(key.getBytes());
            long shards = 2 * totalElements / shardSize;
            shardId = Math.abs(((int) crc.getValue()) % shards);
        }
        return base + ':' + shardId;
    }
}
