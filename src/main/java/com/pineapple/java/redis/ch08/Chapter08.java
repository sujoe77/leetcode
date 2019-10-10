package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;

public class Chapter08 {

    public static final void main(String[] args) throws InterruptedException {
        new Chapter08().run();
    }

    public void run() throws InterruptedException {
        Jedis conn = new Jedis("localhost");
        conn.select(15);
        conn.flushDB();

        User user = new User();
        user.testCreateUserAndStatus(conn);
        conn.flushDB();

        user.testFollowUnfollowUser(conn);
        conn.flushDB();

        user.testSyndicateStatus(conn);
        conn.flushDB();

        user.testRefillTimeline(conn);
    }
















}
