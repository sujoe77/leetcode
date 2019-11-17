package com.pineapple.java.thread.pingpong.ping;

public class Player implements Runnable {
    private static final int MAX_COUNT = 10;
    private final String sound;
    private final boolean turn;


    public Player(String sound, boolean turn) {
        this.sound = sound;
        this.turn = turn;
    }

    @Override
    public void run() {
        for (int i = 0; i < MAX_COUNT; i++) {
            synchronized (Player.class) {
                while (PingAndPong.turn != turn) {
                    try {
                        Player.class.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(sound);
                PingAndPong.turn = !turn;
                Player.class.notifyAll();
            }
        }
    }
}
