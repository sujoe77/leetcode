package com.pineapple.java.redis.ch07;

import com.pineapple.java.redis.ch07.entity.Ecpm;
import com.pineapple.java.redis.ch07.util.set.SetOperation;
import com.pineapple.java.redis.ch07.util.StringUtil;
import com.pineapple.java.redis.ch07.util.set.ZSetOperation;
import org.javatuples.Pair;
import com.pineapple.java.redis.clients.jedis.Jedis;
import com.pineapple.java.redis.clients.jedis.Transaction;
import com.pineapple.java.redis.clients.jedis.Tuple;
import com.pineapple.java.redis.clients.jedis.ZParams;

import java.util.*;

import static com.pineapple.java.redis.ch07.Chapter07.CONTENT;

public class Ad {
    public static Map<Ecpm, Double> AVERAGE_PER_1K = new HashMap<Ecpm, Double>();

    public void testIndexAndTargetAds(Jedis conn) {
        System.out.println("\n----- testIndexAndTargetAds -----");
        Ad ad = new Ad();
        ad.indexAd(conn, "1", new String[]{"USA", "CA"}, CONTENT, Ecpm.CPC, .25);
        ad.indexAd(conn, "2", new String[]{"USA", "VA"}, CONTENT + " wooooo", Ecpm.CPC, .125);

        String[] usa = new String[]{"USA"};
        for (int i = 0; i < 100; i++) {
            ad.targetAds(conn, usa, CONTENT);
        }
        Pair<Long, String> result = ad.targetAds(conn, usa, CONTENT);
        long targetId = result.getValue0();
        String adId = result.getValue1();
        assert "1".equals(result.getValue1());

        result = ad.targetAds(conn, new String[]{"VA"}, "wooooo");
        assert "2".equals(result.getValue1());

        Iterator<Tuple> range = conn.zrangeWithScores("idx:ad:value:", 0, -1).iterator();
        assert new Tuple("2", 0.125).equals(range.next());
        assert new Tuple("1", 0.25).equals(range.next());

        range = conn.zrangeWithScores("ad:base_value:", 0, -1).iterator();
        assert new Tuple("2", 0.125).equals(range.next());
        assert new Tuple("1", 0.25).equals(range.next());

        ad.recordClick(conn, targetId, adId, false);

        range = conn.zrangeWithScores("idx:ad:value:", 0, -1).iterator();
        assert new Tuple("2", 0.125).equals(range.next());
        assert new Tuple("1", 2.5).equals(range.next());

        range = conn.zrangeWithScores("ad:base_value:", 0, -1).iterator();
        assert new Tuple("2", 0.125).equals(range.next());
        assert new Tuple("1", 0.25).equals(range.next());
    }

    public void indexAd(Jedis conn, String id, String[] locations, String content, Ecpm type, double value) {
        Transaction trans = conn.multi();

        for (String location : locations) {
            trans.sadd("idx:req:" + location, id);
        }

        Set<String> words = StringUtil.tokenize(content);
        for (String word : StringUtil.tokenize(content)) {
            trans.zadd("idx:" + word, 0, id);
        }


        double avg = AVERAGE_PER_1K.containsKey(type) ? AVERAGE_PER_1K.get(type) : 1;
        double rvalue = Ecpm.toEcpm(type, 1000, avg, value);

        trans.hset("type:", id, type.name().toLowerCase());
        trans.zadd("idx:ad:value:", rvalue, id);
        trans.zadd("ad:base_value:", value, id);
        for (String word : words) {
            trans.sadd("terms:" + id, word);
        }
        trans.exec();
    }

    @SuppressWarnings("unchecked")
    public Pair<Long, String> targetAds(Jedis conn, String[] locations, String content) {
        Transaction trans = conn.multi();

        String matchedAds = matchLocation(trans, locations);

        String baseEcpm = ZSetOperation.zintersect(
                trans, 30, new ZParams().weights(0, 1), matchedAds, "ad:value:");

        Pair<Set<String>, String> result = finishScoring(trans, matchedAds, baseEcpm, content);

        trans.incr("ads:served:");
        trans.zrevrange("idx:" + result.getValue1(), 0, 0);

        List<Object> response = trans.exec();
        long targetId = (Long) response.get(response.size() - 2);
        Set<String> targetedAds = (Set<String>) response.get(response.size() - 1);

        if (targetedAds.size() == 0) {
            return new Pair<Long, String>(null, null);
        }

        String adId = targetedAds.iterator().next();
        recordTargetingResult(conn, targetId, adId, result.getValue0());

        return new Pair<Long, String>(targetId, adId);
    }

    public String matchLocation(Transaction trans, String[] locations) {
        String[] required = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            required[i] = "req:" + locations[i];
        }
        return SetOperation.union(trans, 300, required);
    }

    public Pair<Set<String>, String> finishScoring(Transaction trans, String matched, String base, String content) {
        Map<String, Integer> bonusEcpm = new HashMap<String, Integer>();
        Set<String> words = StringUtil.tokenize(content);
        for (String word : words) {
            String wordBonus = ZSetOperation.zintersect(
                    trans, 30, new ZParams().weights(0, 1), matched, word);
            bonusEcpm.put(wordBonus, 1);
        }

        if (bonusEcpm.size() > 0) {

            String[] keys = new String[bonusEcpm.size()];
            int[] weights = new int[bonusEcpm.size()];
            int index = 0;
            for (Map.Entry<String, Integer> bonus : bonusEcpm.entrySet()) {
                keys[index] = bonus.getKey();
                weights[index] = bonus.getValue();
                index++;
            }

            ZParams minParams = new ZParams().aggregate(ZParams.Aggregate.MIN).weights(weights);
            String minimum = ZSetOperation.zunion(trans, 30, minParams, keys);

            ZParams maxParams = new ZParams().aggregate(ZParams.Aggregate.MAX).weights(weights);
            String maximum = ZSetOperation.zunion(trans, 30, maxParams, keys);

            String result = ZSetOperation.zunion(
                    trans, 30, new ZParams().weights(2, 1, 1), base, minimum, maximum);
            return new Pair<Set<String>, String>(words, result);
        }
        return new Pair<Set<String>, String>(words, base);
    }

    public void recordTargetingResult(Jedis conn, long targetId, String adId, Set<String> words) {
        Set<String> terms = conn.smembers("terms:" + adId);
        String type = conn.hget("type:", adId);

        Transaction trans = conn.multi();
        terms.addAll(words);
        if (terms.size() > 0) {
            String matchedKey = "terms:matched:" + targetId;
            for (String term : terms) {
                trans.sadd(matchedKey, term);
            }
            trans.expire(matchedKey, 900);
        }

        trans.incr("type:" + type + ":views:");
        for (String term : terms) {
            trans.zincrby("views:" + adId, 1, term);
        }
        trans.zincrby("views:" + adId, 1, "");

        List<Object> response = trans.exec();
        double views = (Double) response.get(response.size() - 1);
        if ((views % 100) == 0) {
            updateCpms(conn, adId);
        }
    }

    @SuppressWarnings("unchecked")
    public void updateCpms(Jedis conn, String adId) {
        Transaction trans = conn.multi();
        trans.hget("type:", adId);
        trans.zscore("ad:base_value:", adId);
        trans.smembers("terms:" + adId);
        List<Object> response = trans.exec();
        String type = (String) response.get(0);
        Double baseValue = (Double) response.get(1);
        Set<String> words = (Set<String>) response.get(2);

        String which = "clicks";
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());
        if (Ecpm.CPA.equals(ecpm)) {
            which = "actions";
        }

        trans = conn.multi();
        trans.get("type:" + type + ":views:");
        trans.get("type:" + type + ':' + which);
        response = trans.exec();
        String typeViews = (String) response.get(0);
        String typeClicks = (String) response.get(1);

        AVERAGE_PER_1K.put(ecpm,
                1000. *
                        Integer.valueOf(typeClicks != null ? typeClicks : "1") /
                        Integer.valueOf(typeViews != null ? typeViews : "1"));

        if (Ecpm.CPM.equals(ecpm)) {
            return;
        }

        String viewKey = "views:" + adId;
        String clickKey = which + ':' + adId;

        trans = conn.multi();
        trans.zscore(viewKey, "");
        trans.zscore(clickKey, "");
        response = trans.exec();
        Double adViews = (Double) response.get(0);
        Double adClicks = (Double) response.get(1);

        double adEcpm = 0;
        if (adClicks == null || adClicks < 1) {
            Double score = conn.zscore("idx:ad:value:", adId);
            adEcpm = score != null ? score.doubleValue() : 0;
        } else {
            adEcpm = Ecpm.toEcpm(
                    ecpm,
                    adViews != null ? adViews.doubleValue() : 1,
                    adClicks != null ? adClicks.doubleValue() : 0,
                    baseValue);
            conn.zadd("idx:ad:value:", adEcpm, adId);
        }
        for (String word : words) {
            trans = conn.multi();
            trans.zscore(viewKey, word);
            trans.zscore(clickKey, word);
            response = trans.exec();
            Double views = (Double) response.get(0);
            Double clicks = (Double) response.get(1);

            if (clicks == null || clicks < 1) {
                continue;
            }

            double wordEcpm = Ecpm.toEcpm(
                    ecpm,
                    views != null ? views.doubleValue() : 1,
                    clicks != null ? clicks.doubleValue() : 0,
                    baseValue);
            double bonus = wordEcpm - adEcpm;
            conn.zadd("idx:" + word, bonus, adId);
        }
    }

    public void recordClick(Jedis conn, long targetId, String adId, boolean action) {
        String type = conn.hget("type:", adId);
        Ecpm ecpm = Enum.valueOf(Ecpm.class, type.toUpperCase());

        String clickKey = "clicks:" + adId;
        String matchKey = "terms:matched:" + targetId;
        Set<String> matched = conn.smembers(matchKey);
        matched.add("");

        Transaction trans = conn.multi();
        if (Ecpm.CPA.equals(ecpm)) {
            trans.expire(matchKey, 900);
            if (action) {
                clickKey = "actions:" + adId;
            }
        }

        if (action && Ecpm.CPA.equals(ecpm)) {
            trans.incr("type:" + type + ":actions:");
        } else {
            trans.incr("type:" + type + ":clicks:");
        }

        for (String word : matched) {
            trans.zincrby(clickKey, 1, word);
        }
        trans.exec();

        updateCpms(conn, adId);
    }
}
