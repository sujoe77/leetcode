package com.pineapple.java.algo.hash.consistent.hash;

import org.testng.annotations.Test;

public class JumpHashTest {

    @Test
    public void testConsistentHash() {
        for (int i = 0; i < 10; i++) {
            int ret = JumpHash.consistentHash(i, 8);
            System.out.println(ret);
        }
    }
}