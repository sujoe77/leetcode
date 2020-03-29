package com.festina;

/**
 * This code is from https://www.geeksforgeeks.org/lru-cache-implementation/
 * This code is originally contributed by Gaurav Tiwari
 * with some modifications
 */

/* We can use Java inbuilt Deque as a double
ended queue to store the cache keys, with
the descending time of reference from front
to back and a set container to check presence
of a key. But remove a key from the Deque using
remove(), it takes O(N) time. This can be
optimized by storing a reference (iterator) to
each key in a hash map. */

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class LRUCache {
    // maximum capacity of cache
    private static final int MAX_SIZE = 4;
    // store keys of cache
    private final Deque<Integer> keyQueue;
    // store references of key in cache
    private final HashMap<Integer, String> cache;

    public LRUCache() {
        keyQueue = new LinkedList<>();
        cache = new HashMap<>();
    }

    public String get(int key) {
        if (!cache.keySet().contains(key)) {
            if (keyQueue.size() == MAX_SIZE) {
                int last = keyQueue.removeLast();
                cache.remove(last);
            }
            String value = computerValue(key);
            if (StringUtils.isNotEmpty(value)) {
                cache.put(key, value);
            }
        } else {
            keyQueue.removeLastOccurrence(key);
        }
        keyQueue.push(key);
        return cache.get(key);
    }

    private String computerValue(int key) {
        //some time consuming calculation
        try {
            Thread.sleep(1000);
        } catch (Exception e){
        }
        return "value of " + key;
    }

    // display contents of cache
    public void display() {
        for (int key : keyQueue) {
            System.out.println(key + "->" + cache.get(key));
        }
    }

    public static void main(String[] args) {
        LRUCache ca = new LRUCache();
        ca.get(1);
        ca.get(2);
        ca.get(3);
        ca.get(1);
        ca.get(4);
        ca.get(5);
        ca.get(6);
        ca.get(100);
        ca.get(10);
        ca.display();
    }
}

