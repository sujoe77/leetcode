package com.pineapple.java.thread.util.exchanger;

import java.util.concurrent.Exchanger;

public class ExchangerDemo {
    static Exchanger<StringBuffer> exchanger = new Exchanger<StringBuffer>();

    public static void main(String[] args) {
        new Thread(new FillingLoop(exchanger)).start();
        new Thread(new EmptyingLoop(exchanger)).start();
    }
}
