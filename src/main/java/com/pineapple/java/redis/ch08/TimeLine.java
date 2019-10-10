package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.pineapple.java.redis.ch08.ThreadUtil.executeLater;
import static com.pineapple.java.redis.ch08.User.*;

public class TimeLine {
    public void refillTimeline(Jedis conn, String incoming, String timeline, User user) {
        refillTimeline(conn, incoming, timeline, 0, user);
    }

    @SuppressWarnings("unchecked")
    public void refillTimeline(Jedis conn, String incoming, String timeline, double start, User user) {
        if (start == 0 && conn.zcard(timeline) >= 750) {
            return;
        }

        Set<Tuple> users = conn.zrangeByScoreWithScores(incoming, String.valueOf(start), "inf", 0, REFILL_USERS_STEP);

        Pipeline pipeline = conn.pipelined();
        for (Tuple tuple : users) {
            String uid = tuple.getElement();
            start = tuple.getScore();
            pipeline.zrevrangeWithScores(
                    "profile:" + uid, 0, HOME_TIMELINE_SIZE - 1);
        }

        List<Object> response = pipeline.syncAndReturnAll();
        List<Tuple> messages = new ArrayList<Tuple>();
        for (Object results : response) {
            messages.addAll((Set<Tuple>) results);
        }

        Collections.sort(messages);
        messages = messages.subList(0, HOME_TIMELINE_SIZE);

        Transaction trans = conn.multi();
        if (messages.size() > 0) {
            for (Tuple tuple : messages) {
                trans.zadd(timeline, tuple.getScore(), tuple.getElement());
            }
        }
        trans.zremrangeByRank(timeline, 0, 0 - HOME_TIMELINE_SIZE - 1);
        trans.exec();

        if (users.size() >= REFILL_USERS_STEP) {
            try {
                Method method = getClass().getDeclaredMethod("refillTimeline", Jedis.class, String.class, String.class, Double.TYPE);
                executeLater("default", method, user, incoming, timeline, start);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void cleanTimelines(Jedis conn, long uid, long statusId, User user) {
        cleanTimelines(conn, uid, statusId, 0, false, user);
    }

    public void cleanTimelines(Jedis conn, long uid, long statusId, double start, boolean onLists, User user) {
        String key = "followers:" + uid;
        String base = "home:";
        if (onLists) {
            key = "list:out:" + uid;
            base = "list:statuses:";
        }
        Set<Tuple> followers = conn.zrangeByScoreWithScores(
                key, String.valueOf(start), "inf", 0, POSTS_PER_PASS);

        Transaction trans = conn.multi();
        for (Tuple tuple : followers) {
            start = tuple.getScore();
            String follower = tuple.getElement();
            trans.zrem(base + follower, String.valueOf(statusId));
        }
        trans.exec();

        Method method = null;
        try {
            method = getClass().getDeclaredMethod(
                    "cleanTimelines", Jedis.class,
                    Long.TYPE, Long.TYPE, Double.TYPE, Boolean.TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (followers.size() >= POSTS_PER_PASS) {
            executeLater("default", method, user, uid, statusId, start, onLists);

        } else if (!onLists) {
            executeLater("default", method, user, uid, statusId, 0, true);
        }
    }
}
