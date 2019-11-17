package com.pineapple.java.thread.pingpong.atom;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player implements Runnable {
    private static final int MAX_COUNT = 10;
    private static AtomicBoolean go = new AtomicBoolean(true);
    private static final Object lock = new Object();

    private final String sound;
    private final boolean flag;


    public Player(String sound) {
        this.sound = sound;
        flag = "ping".equals(sound);
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_COUNT; i++) {
            synchronized (lock) {
                System.out.println(sound + " get lock!");
                while (go.get() != flag) {
                    try {
                        System.out.println(sound + " wait!");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(go.compareAndSet(flag, !flag)) {
                    System.out.println(sound);
                    System.out.println(sound + " send notify!");
                    lock.notifyAll();
                }
            }
        }
    }
}

