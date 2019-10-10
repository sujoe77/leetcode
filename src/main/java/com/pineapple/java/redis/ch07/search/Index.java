package com.pineapple.java.redis.ch07.search;

import com.pineapple.java.redis.ch07.Chapter07;
import com.pineapple.java.redis.ch07.util.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Index {

    public void testIndexDocument(Jedis conn) {
        System.out.println("\n----- testIndexDocument -----");

        System.out.println("We're tokenizing some content...");
        Set<String> tokens = StringUtil.tokenize(Chapter07.CONTENT);
        System.out.println("Those tokens are: " + Arrays.toString(tokens.toArray()));
        assert tokens.size() > 0;

        System.out.println("And now we are indexing that content...");
        int count = indexDocument(conn, "test", Chapter07.CONTENT);
        verifyIndex(conn, tokens, count);
    }

    public int indexDocument(Jedis conn, String docid, String content) {
        Set<String> words = StringUtil.tokenize(content);
        Transaction trans = conn.multi();
        for (String word : words) {
            trans.sadd("idx:" + word, docid);
        }
        return trans.exec().size();
    }

    public void verifyIndex(Jedis conn, Set<String> tokens, int count) {
        assert count == tokens.size();
        Set<String> test = new HashSet<String>();
        test.add("test");
        for (String t : tokens) {
            Set<String> members = conn.smembers("idx:" + t);
            assert test.equals(members);
        }
    }
}
