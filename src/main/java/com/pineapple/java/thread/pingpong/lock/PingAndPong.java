package com.pineapple.java.thread.pingpong.lock;

import java.util.concurrent.locks.LockSupport;

public class PingAndPong {
    public static void main(String[] args) {
        Player p1 = new Player("ping", null);
        Player p2 = new Player("pong", p1);
        p1.setPartner(p2);
        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        t1.start();
        t2.start();
        p1.setGo(true);
        LockSupport.unpark(t1);
    }
}
