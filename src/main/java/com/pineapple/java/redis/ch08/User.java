package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pineapple.java.redis.ch08.ThreadUtil.releaseLock;

public class User {
    public static int HOME_TIMELINE_SIZE = 1000;
    public static int POSTS_PER_PASS = 1000;
    public static int REFILL_USERS_STEP = 50;

    public void testFollowUnfollowUser(Jedis conn) {
        System.out.println("\n----- testFollowUnfollowUser -----");

        assert createUser(conn, "TestUser", "Test User") == 1;
        assert createUser(conn, "TestUser2", "Test User2") == 2;

        assert followUser(conn, 1, 2);
        assert conn.zcard("followers:2") == 1;
        assert conn.zcard("followers:1") == 0;
        assert conn.zcard("following:1") == 1;
        assert conn.zcard("following:2") == 0;
        assert "1".equals(conn.hget("user:1", "following"));
        assert "0".equals(conn.hget("user:2", "following"));
        assert "0".equals(conn.hget("user:1", "followers"));
        assert "1".equals(conn.hget("user:2", "followers"));

        assert !unfollowUser(conn, 2, 1);
        assert unfollowUser(conn, 1, 2);
        assert conn.zcard("followers:2") == 0;
        assert conn.zcard("followers:1") == 0;
        assert conn.zcard("following:1") == 0;
        assert conn.zcard("following:2") == 0;
        assert "0".equals(conn.hget("user:1", "following"));
        assert "0".equals(conn.hget("user:2", "following"));
        assert "0".equals(conn.hget("user:1", "followers"));
        assert "0".equals(conn.hget("user:2", "followers"));
    }

    public void testCreateUserAndStatus(Jedis conn) {
        System.out.println("\n----- testCreateUserAndStatus -----");

        assert createUser(conn, "TestUser", "Test User") == 1;
        assert createUser(conn, "TestUser", "Test User2") == -1;

        assert new Status().createStatus(conn, 1, "This is a new status message") == 1;
        assert "1".equals(conn.hget("user:1", "posts"));
    }

    public void testSyndicateStatus(Jedis conn) throws InterruptedException {
        System.out.println("\n----- testSyndicateStatus -----");

        Status status = new Status();
        assert createUser(conn, "TestUser", "Test User") == 1;
        assert createUser(conn, "TestUser2", "Test User2") == 2;

        assert followUser(conn, 1, 2);
        assert conn.zcard("followers:2") == 1;
        assert "1".equals(conn.hget("user:1", "following"));
        assert status.postStatus(conn, 2, "this is some message content", this) == 1;
        assert status.getStatusMessages(conn, 1).size() == 1;

        for (int i = 3; i < 11; i++) {
            assert createUser(conn, "TestUser" + i, "Test User" + i) == i;
            followUser(conn, i, 2);
        }

        POSTS_PER_PASS = 5;

        assert status.postStatus(conn, 2, "this is some other message content", this) == 2;
        Thread.sleep(100);
        assert status.getStatusMessages(conn, 9).size() == 2;

        assert unfollowUser(conn, 1, 2);
        assert status.getStatusMessages(conn, 1).size() == 0;
    }

    public void testRefillTimeline(Jedis conn) throws InterruptedException {
        Status status = new Status();
        System.out.println("\n----- testRefillTimeline -----");

        assert createUser(conn, "TestUser", "Test User") == 1;
        assert createUser(conn, "TestUser2", "Test User2") == 2;
        assert createUser(conn, "TestUser3", "Test User3") == 3;

        assert followUser(conn, 1, 2);
        assert followUser(conn, 1, 3);

        HOME_TIMELINE_SIZE = 5;

        for (int i = 0; i < 10; i++) {
            assert status.postStatus(conn, 2, "message", this) != -1;
            assert status.postStatus(conn, 3, "message", this) != -1;
            Thread.sleep(50);
        }

        assert status.getStatusMessages(conn, 1).size() == 5;
        assert unfollowUser(conn, 1, 2);
        assert status.getStatusMessages(conn, 1).size() < 5;

        TimeLine timeLine = new TimeLine();
        timeLine.refillTimeline(conn, "following:1", "home:1", this);
        List<Map<String, String>> messages = status.getStatusMessages(conn, 1);
        assert messages.size() == 5;
        for (Map<String, String> message : messages) {
            assert "3".equals(message.get("uid"));
        }

        long statusId = Long.valueOf(messages.get(messages.size() - 1).get("id"));
        assert new Status().deleteStatus(conn, 3, statusId);
        assert status.getStatusMessages(conn, 1).size() == 4;
        assert conn.zcard("home:1") == 5;
        timeLine.cleanTimelines(conn, 3, statusId, this);
        assert conn.zcard("home:1") == 4;
    }

    /**
     * put in hash with user name and id map
     * set in hash user properties
     * @param conn
     * @param login
     * @param name
     * @return
     */
    public long createUser(Jedis conn, String login, String name) {
        String llogin = login.toLowerCase();
        String lock = ThreadUtil.acquireLockWithTimeout(conn, "user:" + llogin, 10, 1);
        if (lock == null) {
            return -1;
        }

        if (conn.hget("users:", llogin) != null) {
            return -1;
        }

        long id = conn.incr("user:id:");
        Transaction trans = conn.multi();
        trans.hset("users:", llogin, String.valueOf(id));
        trans.hmset("user:" + id, getInitUserProperties(login, name, id));
        trans.exec();
        releaseLock(conn, "user:" + llogin, lock);
        return id;
    }

    private Map<String, String> getInitUserProperties(String login, String name, long id) {
        Map<String, String> values = new HashMap<String, String>();
        values.put("login", login);
        values.put("id", String.valueOf(id));
        values.put("name", name);
        values.put("followers", "0");
        values.put("following", "0");
        values.put("posts", "0");
        values.put("signup", String.valueOf(System.currentTimeMillis()));
        return values;
    }

    @SuppressWarnings("unchecked")
    public boolean followUser(Jedis conn, long uid, long otherUid) {
        String fkey1 = "following:" + uid;
        String fkey2 = "followers:" + otherUid;

        if (conn.zscore(fkey1, String.valueOf(otherUid)) != null) {
            return false;
        }

        long now = System.currentTimeMillis();

        Transaction trans = conn.multi();
        trans.zadd(fkey1, now, String.valueOf(otherUid));
        trans.zadd(fkey2, now, String.valueOf(uid));
        trans.zcard(fkey1);
        trans.zcard(fkey2);
        trans.zrevrangeWithScores("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);

        List<Object> response = trans.exec();
        long following = (Long) response.get(response.size() - 3);
        long followers = (Long) response.get(response.size() - 2);
        Set<Tuple> statuses = (Set<Tuple>) response.get(response.size() - 1);

        trans = conn.multi();
        trans.hset("user:" + uid, "following", String.valueOf(following));
        trans.hset("user:" + otherUid, "followers", String.valueOf(followers));
        if (statuses.size() > 0) {
            for (Tuple status : statuses) {
                trans.zadd("home:" + uid, status.getScore(), status.getElement());
            }
        }
        trans.zremrangeByRank("home:" + uid, 0, 0 - HOME_TIMELINE_SIZE - 1);
        trans.exec();

        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean unfollowUser(Jedis conn, long uid, long otherUid) {
        String fkey1 = "following:" + uid;
        String fkey2 = "followers:" + otherUid;

        if (conn.zscore(fkey1, String.valueOf(otherUid)) == null) {
            return false;
        }

        Transaction trans = conn.multi();
        trans.zrem(fkey1, String.valueOf(otherUid));
        trans.zrem(fkey2, String.valueOf(uid));
        trans.zcard(fkey1);
        trans.zcard(fkey2);
        trans.zrevrange("profile:" + otherUid, 0, HOME_TIMELINE_SIZE - 1);

        List<Object> response = trans.exec();
        long following = (Long) response.get(response.size() - 3);
        long followers = (Long) response.get(response.size() - 2);
        Set<String> statuses = (Set<String>) response.get(response.size() - 1);

        trans = conn.multi();
        trans.hset("user:" + uid, "following", String.valueOf(following));
        trans.hset("user:" + otherUid, "followers", String.valueOf(followers));
        if (statuses.size() > 0) {
            for (String status : statuses) {
                trans.zrem("home:" + uid, status);
            }
        }

        trans.exec();
        return true;
    }

}
