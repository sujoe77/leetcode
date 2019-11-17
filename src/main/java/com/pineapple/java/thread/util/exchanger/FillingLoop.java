package com.pineapple.java.thread.util.exchanger;

import java.util.concurrent.Exchanger;

public class FillingLoop implements Runnable {
    StringBuffer initialEmptyBuffer = new StringBuffer("");//...    a made-    up type
    Exchanger<StringBuffer> exchanger;
    int count = 0;

    public FillingLoop(Exchanger<StringBuffer> exchanger) {
        this.exchanger = exchanger;
    }

    public void run() {
        StringBuffer currentBuffer = initialEmptyBuffer;
        try {
            while (currentBuffer != null && count < 10) {
                currentBuffer = exchanger.exchange(currentBuffer);
                addToBuffer(currentBuffer);
            }
        } catch (InterruptedException ex) {
        }
    }

    private void addToBuffer(StringBuffer currentBuffer) {
        currentBuffer.delete(0, currentBuffer.length());
        currentBuffer.append("ping");
        System.out.println(currentBuffer.toString());
        count++;
    }
}