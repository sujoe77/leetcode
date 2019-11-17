package com.pineapple.java.thread.util.phaser;

import com.pineapple.java.thread.util.phaser.thread.FileReaderThread;
import com.pineapple.java.thread.util.phaser.thread.QueryThread;

import java.util.concurrent.Phaser;

public class PhaserDemo {

    public static void main(String[] args) {
        Phaser phaser = new Phaser(1);
        int curPhase = phaser.getPhase();

        doPhaseOne(phaser, curPhase);

        phaser.arriveAndAwaitAdvance();

        doPhaseTwo(phaser);
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        System.out.println("Phase " + curPhase + " completed");
        // deregistering the main thread
        phaser.arriveAndDeregister();
    }

    private static void doPhaseTwo(Phaser phaser) {
        System.out.println("New phase " + phaser.getPhase() + " started");
        // Threads for second phase
        new QueryThread("thread-1", 40, phaser);
        new QueryThread("thread-2", 40, phaser);
    }

    private static void doPhaseOne(Phaser phaser, int curPhase) {
        System.out.println("Phase in Main " + curPhase + " started");
        // Threads for first phase
        new FileReaderThread("thread-1", "file-1", phaser);
        new FileReaderThread("thread-2", "file-2", phaser);
        new FileReaderThread("thread-3", "file-3", phaser);
        //For main thread
    }
}

