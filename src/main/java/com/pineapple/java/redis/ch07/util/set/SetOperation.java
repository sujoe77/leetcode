package com.pineapple.java.redis.ch07.util.set;

import com.pineapple.java.redis.ch07.search.Index;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.pineapple.java.redis.ch07.Chapter07.CONTENT;

public class SetOperation {

    public void testSetOperations(Jedis conn) {
        System.out.println("\n----- testSetOperations -----");
        new Index().indexDocument(conn, "test", CONTENT);

        Set<String> test = new HashSet<String>();
        test.add("test");

        Transaction trans = conn.multi();
        String id = SetOperation.intersect(trans, 30, "content", "indexed");
        trans.exec();
        assert test.equals(conn.smembers("idx:" + id));

        trans = conn.multi();
        id = SetOperation.intersect(trans, 30, "content", "ignored");
        trans.exec();
        assert conn.smembers("idx:" + id).isEmpty();

        trans = conn.multi();
        id = SetOperation.union(trans, 30, "content", "ignored");
        trans.exec();
        assert test.equals(conn.smembers("idx:" + id));

        trans = conn.multi();
        id = SetOperation.difference(trans, 30, "content", "ignored");
        trans.exec();
        assert test.equals(conn.smembers("idx:" + id));

        trans = conn.multi();
        id = SetOperation.difference(trans, 30, "content", "indexed");
        trans.exec();
        assert conn.smembers("idx:" + id).isEmpty();
    }

    public static String intersect(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sinterstore", ttl, items);
    }

    public static String union(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sunionstore", ttl, items);
    }

    public static String difference(Transaction trans, int ttl, String... items) {
        return setCommon(trans, "sdiffstore", ttl, items);
    }

    private static String setCommon(Transaction trans, String method, int ttl, String... items) {
        String[] keys = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            keys[i] = "idx:" + items[i];
        }

        String id = UUID.randomUUID().toString();
        try {
            trans.getClass()
                    .getDeclaredMethod(method, String.class, String[].class)
                    .invoke(trans, "idx:" + id, keys);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trans.expire("idx:" + id, ttl);
        return id;
    }
}
