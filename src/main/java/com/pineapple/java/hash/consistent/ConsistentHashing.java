package com.pineapple.java.hash.consistent;

/**
 * from: https://gist.github.com/VarunVats9/5b785b7aed29eddfaf3436f16f607bcc
 */
public class ConsistentHashing {

    public static void main(String[] args) throws InterruptedException {
        // Load balancer assigning the users to specific server.
        Thread t1 = new Thread(new LoadBalancerTask());
        t1.start();
        t1.join();
    }
}
