package com.pineapple.java.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 4 ways of creating thread pool
 * https://blog.csdn.net/u011974987/article/details/51027795
 */
public class Pools {
    private enum Mode {
        CACHED, FIXED, TIMER, SINGLE, STEAL
    }

    public static void main(String[] args) {
        run(Mode.STEAL);
    }

    private static void run(Mode mode) {
        Executor pool = createPool(mode);
        for (int i = 0; i < 10; i++) {
            final int index = i;
            sleep(1000);
            if (mode == Mode.TIMER) {
                ((ScheduledExecutorService) pool).schedule(() -> threadWork(index), 1, SECONDS);
            } else {
                pool.execute(() -> threadWork(index));
            }
        }
    }

    private static Executor createPool(Mode mode) {
        switch (mode) {
            case CACHED:
                return newCachedThreadPool();
            case FIXED:
                return newFixedThreadPool(3);
            case TIMER:
                return newScheduledThreadPool(5);
            case STEAL:
                return newWorkStealingPool();
            case SINGLE:
            default:
                return newSingleThreadExecutor();
        }
    }

    private static void threadWork(int index) {
        System.out.println("thread id is: " + Thread.currentThread().getId() + " index: " + index);
        sleep(1000);
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
