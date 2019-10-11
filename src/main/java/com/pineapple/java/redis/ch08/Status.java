package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Method;
import java.util.*;

import static com.pineapple.java.redis.ch08.ThreadUtil.*;
import static com.pineapple.java.redis.ch08.User.HOME_TIMELINE_SIZE;
import static com.pineapple.java.redis.ch08.User.POSTS_PER_PASS;

public class Status {

    public boolean deleteStatus(Jedis conn, long uid, long statusId) {
        String key = "status:" + statusId;
        String lock = acquireLockWithTimeout(conn, key, 1, 10);
        if (lock == null) {
            return false;
        }

        try {
            if (!String.valueOf(uid).equals(conn.hget(key, "uid"))) {
                return false;
            }

            Transaction trans = conn.multi();
            trans.del(key);
            trans.zrem("profile:" + uid, String.valueOf(statusId));
            trans.zrem("home:" + uid, String.valueOf(statusId));
            trans.hincrBy("user:" + uid, "posts", -1);
            trans.exec();

            return true;
        } finally {
            releaseLock(conn, key, lock);
        }
    }


    public long createStatus(Jedis conn, long uid, String message) {
        return createStatus(conn, uid, message, null);
    }

    /**
     * put in hash a new status,
     * increment user posts count in hash
     *
     * @param conn
     * @param uid
     * @param message
     * @param data
     * @return
     */
    public long createStatus(Jedis conn, long uid, String message, Map<String, String> data) {
        Transaction trans = conn.multi();
        trans.hget("user:" + uid, "login");
        trans.incr("status:id:");

        List<Object> response = trans.exec();
        String login = (String) response.get(0);
        long id = (Long) response.get(1);

        if (login == null) {
            return -1;
        }

        data = getStatusDetails(uid, message, data, login, id);

        trans = conn.multi();
        trans.hmset("status:" + id, data);
        trans.hincrBy("user:" + uid, "posts", 1);
        trans.exec();
        return id;
    }

    private Map<String, String> getStatusDetails(long uid, String message, Map<String, String> data, String login, long id) {
        if (data == null) {
            data = new HashMap<String, String>();
        }
        data.put("message", message);
        data.put("posted", String.valueOf(System.currentTimeMillis()));
        data.put("id", String.valueOf(id));
        data.put("uid", String.valueOf(uid));
        data.put("login", login);
        return data;
    }

    public List<Map<String, String>> getStatusMessages(Jedis conn, long uid) {
        return getStatusMessages(conn, uid, 1, 30);
    }

    /**
     * Zet save user's status id, hash set save status content
     * get status ids from zset, and get contents from hash
     *
     * @param conn
     * @param uid
     * @param page
     * @param count
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getStatusMessages(Jedis conn, long uid, int page, int count) {
        Set<String> statusIds = conn.zrevrange("home:" + uid, (page - 1) * count, page * count - 1);

        Transaction trans = conn.multi();
        for (String id : statusIds) {
            trans.hgetAll("status:" + id);
        }

        List<Map<String, String>> statuses = new ArrayList<Map<String, String>>();
        for (Object result : trans.exec()) {
            Map<String, String> status = (Map<String, String>) result;
            if (status != null && status.size() > 0) {
                statuses.add(status);
            }
        }
        return statuses;
    }

    public long postStatus(Jedis conn, long uid, String message, User user) {
        return postStatus(conn, uid, message, null, user);
    }

    public long postStatus(Jedis conn, long uid, String message, Map<String, String> data, User user) {
        long id = new Status().createStatus(conn, uid, message, data);
        if (id == -1) {
            return -1;
        }

        String postedString = conn.hget("status:" + id, "posted");
        if (postedString == null) {
            return -1;
        }

        long posted = Long.parseLong(postedString);
        conn.zadd("profile:" + uid, posted, String.valueOf(id));

        syndicateStatus(conn, uid, id, posted, 0, user);
        return id;
    }

    public void syndicateStatus(Jedis conn, long uid, long statusId, long postTime, double start, User user) {
        Set<Tuple> followers = conn.zrangeByScoreWithScores(
                "followers:" + uid,
                String.valueOf(start), "inf",
                0, POSTS_PER_PASS);

        Transaction trans = conn.multi();
        for (Tuple tuple : followers) {
            String follower = tuple.getElement();
            start = tuple.getScore();
            trans.zadd("home:" + follower, postTime, String.valueOf(statusId));
            trans.zrange("home:" + follower, 0, -1);
            trans.zremrangeByRank("home:" + follower, 0, 0 - HOME_TIMELINE_SIZE - 1);
        }
        trans.exec();

        if (followers.size() >= POSTS_PER_PASS) {
            try {
                Method method = getClass().getDeclaredMethod("syndicateStatus", Jedis.class, Long.TYPE, Long.TYPE, Long.TYPE, Double.TYPE);
                executeLater("default", method, user, uid, statusId, postTime, start);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
