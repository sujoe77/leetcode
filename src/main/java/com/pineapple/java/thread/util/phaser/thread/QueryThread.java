package com.pineapple.java.thread.util.phaser.thread;

import java.util.concurrent.Phaser;

public class QueryThread implements Runnable {
    private String threadName;
    private int param;
    private Phaser ph;

    public QueryThread(String threadName, int param, Phaser ph) {
        this.threadName = threadName;
        this.param = param;
        this.ph = ph;
        ph.register();
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("This is phase " + ph.getPhase());
        System.out.println("Querying DB using param " + param + " Thread " + threadName);
        ph.arriveAndAwaitAdvance();
        System.out.println("Threads finished");
        ph.arriveAndDeregister();
    }
}
