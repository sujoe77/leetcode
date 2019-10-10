package com.pineapple.java.redis.ch02;

import com.pineapple.java.redis.ch02.thread.CleanSessionsThread;
import redis.clients.jedis.Jedis;

public class Token {

    public void updateToken(Jedis conn, String token, String user, String item) {
        long timestamp = System.currentTimeMillis() / 1000;
        conn.hset("login:", token, user);
        conn.zadd("recent:", timestamp, token);
        if (item != null) {
            conn.zadd("viewed:" + token, timestamp, item);
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            conn.zincrby("viewed:", -1, item);
        }
    }

    public String checkToken(Jedis conn, String token) {
        return conn.hget("login:", token);
    }

    public long getLoginCount(Jedis conn) {
        return conn.hlen("login:");
    }

    public void cleanSession() throws InterruptedException {
        CleanSessionsThread thread = new CleanSessionsThread(0);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()) {
            throw new RuntimeException("The clean sessions thread is still alive?!?");
        }
    }
}
