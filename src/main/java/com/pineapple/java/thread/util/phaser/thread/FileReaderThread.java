package com.pineapple.java.thread.util.phaser.thread;

import java.util.concurrent.Phaser;

public class FileReaderThread implements Runnable {
    private String threadName;
    private String fileName;
    private Phaser ph;

    public FileReaderThread(String threadName, String fileName, Phaser ph) {
        this.threadName = threadName;
        this.fileName = fileName;
        this.ph = ph;
        ph.register();
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("This is phase " + ph.getPhase());

        try {
            Thread.sleep(20);
            System.out.println("Reading file " + fileName + " thread "
                    + threadName + " parsing and storing to DB ");
            // Using await and advance so that all thread wait here
            ph.arriveAndAwaitAdvance();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ph.arriveAndDeregister();
    }
}
