package com.pineapple.java.redis.ch09;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class ZipList {
    public static void testLongZiplistPerformance(Jedis conn) {
        System.out.println("\n----- testLongZiplistPerformance -----");

        longZiplistPerformance(conn, Sharding.TEST, 5, 10, 10);
        assert conn.llen(Sharding.TEST) == 5;
    }

    public static double longZiplistPerformance(Jedis conn, String key, int length, int passes, int psize) {
        conn.del(key);
        for (int i = 0; i < length; i++) {
            conn.rpush(key, String.valueOf(i));
        }

        Pipeline pipeline = conn.pipelined();
        long time = System.currentTimeMillis();
        for (int p = 0; p < passes; p++) {
            for (int pi = 0; pi < psize; pi++) {
                pipeline.rpoplpush(key, key);
            }
            pipeline.sync();
        }
        return (passes * psize) / (System.currentTimeMillis() - time);
    }
}
