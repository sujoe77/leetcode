package com.pineapple.java.thread.pingpong.ping3;

import java.util.concurrent.BlockingQueue;

public class Player implements Runnable {
    private final BlockingQueue<String> readQueue;
    private final BlockingQueue<String> writeQueue;
    private final String content;
    private int MAX_COUNT = 5;

    public Player(BlockingQueue<String> readQueue, BlockingQueue<String> writeQueue, String content) {
        this.readQueue = readQueue;
        this.writeQueue = writeQueue;
        this.content = content;
    }

    @Override
    public void run() {
        while (MAX_COUNT-- > 0) {
            try {
                writeQueue.put(content);
//                System.out.println("" + Thread.currentThread().getId() + " put " + content);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                String take = readQueue.take();
                System.out.println("" + Thread.currentThread().getId() + " take " + take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
