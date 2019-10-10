package redis.ch02.thread;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Set;

public class CleanFullSessionsThread extends Thread {
    private Jedis conn;
    private int limit;
    private boolean quit;

    public CleanFullSessionsThread(int limit) {
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
            Set<String> sessionSet = conn.zrange("recent:", 0, endIndex - 1);
            String[] sessions = sessionSet.toArray(new String[sessionSet.size()]);

            ArrayList<String> sessionKeys = new ArrayList<String>();
            for (String sess : sessions) {
                sessionKeys.add("viewed:" + sess);
                sessionKeys.add("cart:" + sess);
            }

            conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
            conn.hdel("login:", sessions);
            conn.zrem("recent:", sessions);
        }
    }
}