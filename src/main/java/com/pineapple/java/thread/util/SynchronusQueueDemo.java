package com.pineapple.java.thread.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class SynchronusQueueDemo {
    static BlockingQueue<String> queue = new SynchronousQueue<>();

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < 10) {
                    try {
                        queue.put("1");
                        System.out.println("ping" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < 10) {
                    try {
                        queue.take();
                        Thread.sleep(10);
                        System.out.println("pong" + i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t1.start();
        t2.start();
    }
}
