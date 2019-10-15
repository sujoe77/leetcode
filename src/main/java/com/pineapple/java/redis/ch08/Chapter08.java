package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;

/**
 * Zet:
 *  profile:uid, post time, time, statusId
 *  home:uid, post time, statusId
 *  following:uid, time, uid
 *  followers:uid, time, uid
 *
 *
 * Hash:
 *  status:statusId, map of status details
 *  user:userId, user details map
 *  users:, user name, user id
 *
 * String
 *  lock, use as distributed lock
 *
 */
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
