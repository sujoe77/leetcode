package com.pineapple.java.thread.pingpong.ping2;

public class Player implements Runnable {
    private final String content;

    public Player(String content) {
        this.content = content;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            synchronized (PingAndPong.lock) {
                System.out.println(content + " " + toString());
                PingAndPong.lock.notifyAll();
                try {
                    PingAndPong.lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
