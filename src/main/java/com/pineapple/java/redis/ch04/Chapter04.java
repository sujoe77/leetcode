package com.pineapple.java.redis.ch04;

import redis.clients.jedis.Jedis;


public class Chapter04 {



    public static final void main(String[] args) {
        new Chapter04().run();
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        Item item = new Item();
        item.testListItem(conn, false);
        item.testPurchaseItem(conn);

        new PipeLine().testBenchmarkUpdateToken(conn);
    }


}
