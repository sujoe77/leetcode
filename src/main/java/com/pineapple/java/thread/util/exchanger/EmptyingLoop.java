package com.pineapple.java.thread.util.exchanger;

import java.util.concurrent.Exchanger;

public class EmptyingLoop implements Runnable {
    StringBuffer initialFullBuffer = new StringBuffer("");//...    a made-    up type;
    Exchanger<StringBuffer> exchanger;
    int count = 0;

    public EmptyingLoop(Exchanger<StringBuffer> exchanger) {
        this.exchanger = exchanger;
    }

    public void run() {
        StringBuffer currentBuffer = initialFullBuffer;
        try {
            while (currentBuffer != null && count < 10) {
                currentBuffer = exchanger.exchange(currentBuffer);
                takeFromBuffer(currentBuffer);
            }
        } catch (InterruptedException ex) {
        }
    }

    private void takeFromBuffer(StringBuffer currentBuffer) {
        currentBuffer.delete(0, currentBuffer.length());
        currentBuffer.append("pong");
        System.out.println(currentBuffer.toString());
        count++;
    }
}