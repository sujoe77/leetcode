package com.pineapple.java.redis.ch02;


import redis.clients.jedis.Jedis;

public class Chapter02 {
    public static final void main(String[] args) throws InterruptedException {
        new Chapter02().run();
    }

    public void run() throws InterruptedException {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        new Login().testLoginCookies(conn);
        new ShoppingCart().testShoppingCartCookies(conn);
        Cache cache = new Cache();
        cache.testCacheRows(conn);
        cache.testCacheRequest(conn);
    }

    public interface Callback {
        public String call(String request);
    }
}
