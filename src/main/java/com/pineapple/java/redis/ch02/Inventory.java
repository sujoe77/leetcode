package com.pineapple.java.redis.ch02;

public class Inventory {
    private String id;
    private String data;
    private long time;

    private Inventory(String id) {
        this.id = id;
        this.data = "data to cache...";
        this.time = System.currentTimeMillis() / 1000;
    }

    public static Inventory get(String id) {
        return new Inventory(id);
    }
}
