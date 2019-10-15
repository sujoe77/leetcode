package com.pineapple.java.thread.ping3;

import java.util.concurrent.LinkedBlockingDeque;

public class PingAndPong {
    public static void main(String[] args) {
        LinkedBlockingDeque<String> q1 = new LinkedBlockingDeque<>(1);
        LinkedBlockingDeque<String> q2 = new LinkedBlockingDeque<>(1);
        Player p1 = new Player(q1, q2, "ping");
        Player p2 = new Player(q2, q1, "pong");
        new Thread(p1).start();
        new Thread(p2).start();
    }
}
