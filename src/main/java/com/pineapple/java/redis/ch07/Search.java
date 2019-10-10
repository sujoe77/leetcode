package com.pineapple.java.redis.ch07;

import com.pineapple.java.redis.ch07.entity.Query;
import com.pineapple.java.redis.ch07.entity.SearchResult;
import com.pineapple.java.redis.ch07.entity.WordScore;
import com.pineapple.java.redis.ch07.search.Index;
import com.pineapple.java.redis.ch07.util.set.SetOperation;
import com.pineapple.java.redis.ch07.util.StringUtil;
import com.pineapple.java.redis.ch07.util.set.ZSetOperation;
import com.pineapple.java.redis.clients.jedis.Jedis;
import com.pineapple.java.redis.clients.jedis.SortingParams;
import com.pineapple.java.redis.clients.jedis.Transaction;
import com.pineapple.java.redis.clients.jedis.ZParams;

import java.util.*;

import static com.pineapple.java.redis.ch07.Chapter07.CONTENT;

public class Search {
    public void testParseQuery(Jedis conn) {
        System.out.println("\n----- testParseQuery -----");
        String queryString = "test query without stopwords";
        Query query = StringUtil.parse(queryString);
        String[] words = queryString.split(" ");
        for (int i = 0; i < words.length; i++) {
            List<String> word = new ArrayList<String>();
            word.add(words[i]);
            assert word.equals(query.all.get(i));
        }
        assert query.unwanted.isEmpty();

        queryString = "test +query without -stopwords";
        query = StringUtil.parse(queryString);
        assert "test".equals(query.all.get(0).get(0));
        assert "query".equals(query.all.get(0).get(1));
        assert "without".equals(query.all.get(1).get(0));
        assert "stopwords".equals(query.unwanted.toArray()[0]);
    }

    public void testParseAndSearch(Jedis conn) {
        System.out.println("\n----- testParseAndSearch -----");
        System.out.println("And now we are testing search...");
        new Index().indexDocument(conn, "test", CONTENT);

        Set<String> test = new HashSet<String>();
        test.add("test");

        String id = parseAndSearch(conn, "content", 30);
        assert test.equals(conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed random", 30);
        assert test.equals(conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content +indexed random", 30);
        assert test.equals(conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed +random", 30);
        assert test.equals(conn.smembers("idx:" + id));

        id = parseAndSearch(conn, "content indexed -random", 30);
        assert conn.smembers("idx:" + id).isEmpty();

        id = parseAndSearch(conn, "content indexed +random", 30);
        assert test.equals(conn.smembers("idx:" + id));

        System.out.println("Which passed!");
    }

    public void testSearchWithSort(Jedis conn) {
        System.out.println("\n----- testSearchWithSort -----");
        System.out.println("And now let's test searching with sorting...");

        new Index().indexDocument(conn, "test", CONTENT);
        new Index().indexDocument(conn, "test2", CONTENT);

        HashMap<String, String> values = new HashMap<String, String>();
        values.put("updated", "12345");
        values.put("id", "10");
        conn.hmset("kb:doc:test", values);

        values.put("updated", "54321");
        values.put("id", "1");
        conn.hmset("kb:doc:test2", values);

        SearchResult result = searchAndSort(conn, "content", "-updated");
        assert "test2".equals(result.results.get(0));
        assert "test".equals(result.results.get(1));

        result = searchAndSort(conn, "content", "-id");
        assert "test".equals(result.results.get(0));
        assert "test2".equals(result.results.get(1));

        System.out.println("Which passed!");
    }

    public void testSearchWithZsort(Jedis conn) {
        System.out.println("\n----- testSearchWithZsort -----");
        System.out.println("And now let's test searching with sorting via zset...");

        new Index().indexDocument(conn, "test", CONTENT);
        new Index().indexDocument(conn, "test2", CONTENT);

        conn.zadd("idx:sort:update", 12345, "test");
        conn.zadd("idx:sort:update", 54321, "test2");
        conn.zadd("idx:sort:votes", 10, "test");
        conn.zadd("idx:sort:votes", 1, "test2");

        Map<String, Integer> weights = new HashMap<String, Integer>();
        weights.put("update", 1);
        weights.put("vote", 0);
        SearchResult result = searchAndZsort(conn, "content", false, weights);
        assert "test".equals(result.results.get(0));
        assert "test2".equals(result.results.get(1));

        weights.put("update", 0);
        weights.put("vote", 1);
        result = searchAndZsort(conn, "content", false, weights);
        assert "test2".equals(result.results.get(0));
        assert "test".equals(result.results.get(1));
        System.out.println("Which passed!");
    }

    public void testStringToScore(Jedis conn) {
        System.out.println("\n----- testStringToScore -----");

        String[] words = "these are some words that will be sorted".split(" ");

        List<WordScore> pairs = new ArrayList<WordScore>();
        for (String word : words) {
            pairs.add(new WordScore(word, StringUtil.stringToScore(word)));
        }
        List<WordScore> pairs2 = new ArrayList<WordScore>(pairs);
        Collections.sort(pairs);
        Collections.sort(pairs2, new Comparator<WordScore>() {
            @Override
            public int compare(WordScore o1, WordScore o2) {
                long diff = o1.score - o2.score;
                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }
        });
        assert pairs.equals(pairs2);

        Map<Integer, Integer> lower = new HashMap<Integer, Integer>();
        lower.put(-1, -1);
        int start = (int) 'a';
        int end = (int) 'z';
        for (int i = start; i <= end; i++) {
            lower.put(i, i - start);
        }

        words = "these are some words that will be sorted".split(" ");
        pairs = new ArrayList<WordScore>();
        for (String word : words) {
            pairs.add(new WordScore(word, StringUtil.stringToScoreGeneric(word, lower)));
        }
        pairs2 = new ArrayList<WordScore>(pairs);
        Collections.sort(pairs);
        Collections.sort(pairs2, new Comparator<WordScore>() {
            @Override
            public int compare(WordScore o1, WordScore o2) {
                long diff = o1.score - o2.score;
                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }
        });
        assert pairs.equals(pairs2);

        Map<String, String> values = new HashMap<String, String>();
        values.put("test", "value");
        values.put("test2", "other");
        zaddString(conn, "key", values);
        assert conn.zscore("key", "test") == StringUtil.stringToScore("value");
        assert conn.zscore("key", "test2") == StringUtil.stringToScore("other");
    }

    public String parseAndSearch(Jedis conn, String queryString, int ttl) {
        Query query = StringUtil.parse(queryString);
        if (query.all.isEmpty()) {
            return null;
        }

        List<String> toIntersect = new ArrayList<String>();
        for (List<String> syn : query.all) {
            if (syn.size() > 1) {
                Transaction trans = conn.multi();
                toIntersect.add(SetOperation.union(trans, ttl, syn.toArray(new String[syn.size()])));
                trans.exec();
            } else {
                toIntersect.add(syn.get(0));
            }
        }

        String intersectResult = null;
        if (toIntersect.size() > 1) {
            Transaction trans = conn.multi();
            intersectResult = SetOperation.intersect(
                    trans, ttl, toIntersect.toArray(new String[toIntersect.size()]));
            trans.exec();
        } else {
            intersectResult = toIntersect.get(0);
        }

        if (!query.unwanted.isEmpty()) {
            String[] keys = query.unwanted
                    .toArray(new String[query.unwanted.size() + 1]);
            keys[keys.length - 1] = intersectResult;
            Transaction trans = conn.multi();
            intersectResult = SetOperation.difference(trans, ttl, keys);
            trans.exec();
        }

        return intersectResult;
    }

    @SuppressWarnings("unchecked")
    public SearchResult searchAndSort(Jedis conn, String queryString, String sort) {
        boolean desc = sort.startsWith("-");
        if (desc) {
            sort = sort.substring(1);
        }
        boolean alpha = !"updated".equals(sort) && !"id".equals(sort);
        String by = "kb:doc:*->" + sort;

        String id = parseAndSearch(conn, queryString, 300);

        Transaction trans = conn.multi();
        trans.scard("idx:" + id);
        SortingParams params = new SortingParams();
        if (desc) {
            params.desc();
        }
        if (alpha) {
            params.alpha();
        }
        params.by(by);
        params.limit(0, 20);
        trans.sort("idx:" + id, params);
        List<Object> results = trans.exec();

        return new SearchResult(
                id,
                ((Long) results.get(0)).longValue(),
                (List<String>) results.get(1));
    }

    @SuppressWarnings("unchecked")
    public SearchResult searchAndZsort(Jedis conn, String queryString, boolean desc, Map<String, Integer> weights) {
        int ttl = 300;
        int start = 0;
        int num = 20;
        String id = parseAndSearch(conn, queryString, ttl);

        int updateWeight = weights.containsKey("update") ? weights.get("update") : 1;
        int voteWeight = weights.containsKey("vote") ? weights.get("vote") : 0;

        String[] keys = new String[]{id, "sort:update", "sort:votes"};
        Transaction trans = conn.multi();
        id = ZSetOperation.zintersect(
                trans, ttl, new ZParams().weights(0, updateWeight, voteWeight), keys);

        trans.zcard("idx:" + id);
        if (desc) {
            trans.zrevrange("idx:" + id, start, start + num - 1);
        } else {
            trans.zrange("idx:" + id, start, start + num - 1);
        }
        List<Object> results = trans.exec();

        return new SearchResult(
                id,
                ((Long) results.get(results.size() - 2)).longValue(),
                // Note: it's a LinkedHashSet, so it's ordered
                new ArrayList<String>((Set<String>) results.get(results.size() - 1)));
    }

    public long zaddString(Jedis conn, String name, Map<String, String> values) {
        Map<Double, String> pieces = new HashMap<Double, String>(values.size());
        for (Map.Entry<String, String> entry : values.entrySet()) {
            pieces.put((double) StringUtil.stringToScore(entry.getValue()), entry.getKey());
        }
        return conn.zadd(name, pieces);
    }
}
