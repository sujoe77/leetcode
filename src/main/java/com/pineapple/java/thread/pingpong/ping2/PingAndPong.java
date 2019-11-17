package com.pineapple.java.thread.pingpong.ping2;

public class PingAndPong {
    public static PingAndPong lock = new PingAndPong();
    public static void main(String[] args) {
        Data data = new Data();
        data.setContent("com/pineapple/java/thread/pingpong/ping");
        Thread t1 = new Thread(new Player("com/pineapple/java/thread/pingpong/ping"));
        Thread t2 = new Thread(new Player("pong"));
        t1.start();
        t2.start();
    }
}
