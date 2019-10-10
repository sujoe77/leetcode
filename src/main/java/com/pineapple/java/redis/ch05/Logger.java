package com.pineapple.java.redis.ch05;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.pineapple.java.redis.ch05.Chapter05.COLLATOR;
import static com.pineapple.java.redis.ch05.Chapter05.ISO_FORMAT;

/**
 * logRecent, log last 100 logs to list
 *
 * use zset to maintain counter of log types
 *
 * user string record start time
 */
public class Logger {
    public static final String DEBUG = "debug";
    public static final String INFO = "info";
    public static final String WARNING = "warning";
    public static final String ERROR = "error";
    public static final String CRITICAL = "critical";
    public static final SimpleDateFormat TIMESTAMP = new SimpleDateFormat("EEE MMM dd HH:00:00 yyyy");


    public void testLogRecent(Jedis conn) {
        System.out.println("\n----- testLogRecent -----");
        System.out.println("Let's write a few logs to the recent log");
        for (int i = 0; i < 5; i++) {
            logRecent(conn, "test", "this is message " + i);
        }
        List<String> recent = conn.lrange("recent:test:info", 0, -1);
        System.out.println("The current recent message log has this many messages: " + recent.size());
        System.out.println("Those messages include:");
        for (String message : recent) {
            System.out.println(message);
        }
        assert recent.size() >= 5;
    }

    public void testLogCommon(Jedis conn) {
        System.out.println("\n----- testLogCommon -----");
        System.out.println("Let's write some items to the common log");
        for (int count = 1; count < 6; count++) {
            for (int i = 0; i < count; i++) {
                logCommon(conn, "test", "message-" + count);
            }
        }
        Set<Tuple> common = conn.zrevrangeWithScores("common:test:info", 0, -1);
        System.out.println("The current number of common messages is: " + common.size());
        System.out.println("Those common messages are:");
        for (Tuple tuple : common) {
            System.out.println("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert common.size() >= 5;
    }

    public void logRecent(Jedis conn, String name, String message) {
        logRecent(conn, name, message, INFO);
    }

    public void logRecent(Jedis conn, String name, String message, String severity) {
        String destination = "recent:" + name + ':' + severity;
        Pipeline pipe = conn.pipelined();
        pipe.lpush(destination, TIMESTAMP.format(new Date()) + ' ' + message);
        pipe.ltrim(destination, 0, 99);
        pipe.sync();
    }

    public void logCommon(Jedis conn, String name, String message) {
        logCommon(conn, name, message, INFO, 5000);
    }

    public void logCommon(Jedis conn, String name, String message, String severity, int timeout) {
        String commonDest = "common:" + name + ':' + severity;
        String startKey = commonDest + ":start";
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            conn.watch(startKey);
            String hourStart = ISO_FORMAT.format(new Date());
            String existing = conn.get(startKey);

            Transaction trans = conn.multi();
            if (existing != null && COLLATOR.compare(existing, hourStart) < 0) {
                //rename counter
                trans.rename(commonDest, commonDest + ":last");
                //rename last startkey to :last
                trans.rename(startKey, commonDest + ":pstart");
                //record start time
                trans.set(startKey, hourStart);
            }

            logMessage(name, message, severity, commonDest, trans);
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null) {
                continue;
            }
            return;
        }
    }

    private void logMessage(String name, String message, String severity, String commonDest, Transaction trans) {
        //increase counter
        trans.zincrby(commonDest, 1, message);
        String recentDest = "recent:" + name + ':' + severity;
        //log to list
        trans.lpush(recentDest, TIMESTAMP.format(new Date()) + ' ' + message);
        trans.ltrim(recentDest, 0, 99);
    }

}
