package com.pineapple.java.redis.ch05.stat;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

public class AccessTimer {
    private Jedis conn;
    private long start;

    public AccessTimer(Jedis conn) {
        this.conn = conn;
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void stop(Statistics statistics, String context) {
        long delta = System.currentTimeMillis() - start;
        List<Object> stats = statistics.updateStats(conn, context, "AccessTime", delta / 1000.0);
        double average = (Double) stats.get(1) / (Double) stats.get(0);

        Transaction trans = conn.multi();
        trans.zadd("slowest:AccessTime", average, context);
        trans.zremrangeByRank("slowest:AccessTime", 0, -101);
        trans.exec();
    }
}
