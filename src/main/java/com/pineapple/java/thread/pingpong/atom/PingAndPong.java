package com.pineapple.java.thread.pingpong.atom;

public class PingAndPong {
    public static void main(String[] args) throws InterruptedException {
        Player p1 = new Player("ping");
        Player p2 = new Player("pong");
        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
