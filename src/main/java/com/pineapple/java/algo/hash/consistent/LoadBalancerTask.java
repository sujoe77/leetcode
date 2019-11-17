package com.pineapple.java.algo.hash.consistent;

import java.util.concurrent.ThreadLocalRandom;

public class LoadBalancerTask implements Runnable {

    LoadBalancerTask() {
    }

    @Override
    public void run() {

        while (true) {
            final int randomUserId = ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);

            LoadBalancer.INSTANCE.findServer(randomUserId);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
