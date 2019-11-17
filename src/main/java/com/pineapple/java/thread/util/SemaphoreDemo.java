package com.pineapple.java.thread.util;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
    private static final int MAX_AVAILABLE = 100;
    protected boolean[] used = new boolean[MAX_AVAILABLE];

    private final Semaphore semaphore = new Semaphore(MAX_AVAILABLE, true);

    protected Object[] items = new Object[]{}; //...    whatever kinds of items being managed

    public Object getItem() throws InterruptedException {
        semaphore.acquire();
        return getNextAvailableItem();
    }

    public void putItem(Object x) {
        if (markAsUnused(x))
            semaphore.release();
    }
    // Not a particularly efficient data structure; just for demo

    protected synchronized Object getNextAvailableItem() {
        for (int i = 0; i < MAX_AVAILABLE; ++i) {
            if (!used[i]) {
                used[i] = true;
                return items[i];
            }
        }
        return null; // not reached
    }

    protected synchronized boolean markAsUnused(Object item) {
        for (int i = 0; i < MAX_AVAILABLE; ++i) {
            if (item == items[i]) {
                if (used[i]) {
                    used[i] = false;
                    return true;
                } else
                    return false;
            }
        }
        return false;
    }

}
