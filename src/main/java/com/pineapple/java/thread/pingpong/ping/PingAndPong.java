package com.pineapple.java.thread.pingpong.ping;

public class PingAndPong {
    public static Boolean turn = false;
    public static String lock_ping = "";
    public static String lock_pong = "";
    public static void main(String[] args) {
        Player pong = null;
        Player ping = new Player("com/pineapple/java/thread/pingpong/ping", true);
        pong = new Player("pong", false);
        Thread t1 = new Thread(ping);
        Thread t2 = new Thread(pong);
        t1.start();
        t2.start();
    }
}
