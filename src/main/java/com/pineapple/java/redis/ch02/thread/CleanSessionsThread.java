package com.pineapple.java.redis.ch02.thread;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Set;

public class CleanSessionsThread extends Thread {
    private Jedis conn;
    private int limit;
    private boolean quit;

    public CleanSessionsThread(int limit) {
        this.conn = new Jedis("localhost");
        this.conn.select(15);
        this.limit = limit;
    }

    public void quit() {
        quit = true;
    }

    @Override
    public void run() {
        while (!quit) {
            long size = conn.zcard("recent:");
            if (size <= limit) {
                try {
                    sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            long endIndex = Math.min(size - limit, 100);
            Set<String> tokenSet = conn.zrange("recent:", 0, endIndex - 1);
            String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);

            ArrayList<String> sessionKeys = new ArrayList<String>();
            for (String token : tokens) {
                sessionKeys.add("viewed:" + token);
            }

            conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
            conn.hdel("login:", tokens);
            conn.zrem("recent:", tokens);
        }
    }
}
