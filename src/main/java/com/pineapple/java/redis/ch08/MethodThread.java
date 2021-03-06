package com.pineapple.java.redis.ch08;

import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;

public class MethodThread extends Thread {
    private Object instance;
    private Method method;
    private Object[] args;

    public MethodThread(Object instance, Method method, Object... args) {
        this.instance = instance;
        this.method = method;
        this.args = args;
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        Object[] args = new Object[this.args.length + 1];
        System.arraycopy(this.args, 0, args, 1, this.args.length);
        args[0] = conn;

        try {
            method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}