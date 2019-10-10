package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * user sestnx and expire to implement a lock
 */
public class ThreadUtil {

    public static void executeLater(String queue, Method method, User instance, Object... args) {
        MethodThread thread = new MethodThread(instance, method, args);
        thread.start();
    }

    public static boolean releaseLock(Jedis conn, String lockName, String identifier) {
        lockName = "lock:" + lockName;
        while (true) {
            conn.watch(lockName);
            if (identifier.equals(conn.get(lockName))) {
                Transaction trans = conn.multi();
                trans.del(lockName);
                List<Object> result = trans.exec();
                // null response indicates that the transaction was aborted due
                // to the watched key changing.
                if (result == null) {
                    continue;
                }
                return true;
            }

            conn.unwatch();
            break;
        }

        return false;
    }

    public static String acquireLockWithTimeout(Jedis conn, String lockName, int acquireTimeout, int lockTimeout) {
        String id = UUID.randomUUID().toString();
        lockName = "lock:" + lockName;

        long end = System.currentTimeMillis() + (acquireTimeout * 1000);
        while (System.currentTimeMillis() < end) {
            if (conn.setnx(lockName, id) >= 1) {
                conn.expire(lockName, lockTimeout);
                return id;
            } else if (conn.ttl(lockName) <= 0) {
                conn.expire(lockName, lockTimeout);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                Thread.interrupted();
            }
        }

        return null;
    }
}
