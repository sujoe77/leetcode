package com.pineapple.java.thread.pingpong.lock;

import java.util.concurrent.locks.LockSupport;

public class Player implements Runnable {
    private static final int MAX_COUNT = 10;

    private final String sound;
    private Thread thread;
    private Player partner;
    private boolean go = false;


    public Player(String sound, Player partner) {
        this.sound = sound;
        this.partner = partner;
    }

    public void setGo(boolean go) {
        this.go = go;
    }

    public void setPartner(Player partner) {
        this.partner = partner;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        for (int i = 0; i < MAX_COUNT; i++) {
            while(!go) {
                LockSupport.park(this);
            }
            System.out.println(sound);
            go = false;
            partner.setGo(true);
            LockSupport.unpark(partner.getThread());
        }
    }
}

