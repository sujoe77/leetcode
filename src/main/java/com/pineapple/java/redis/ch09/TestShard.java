package com.pineapple.java.redis.ch09;

import org.testng.annotations.Test;

public class TestShard {
    @Test
    public void testShardKey() {
//        Sharding.testShardKey(null);
        int[] radix = new int[]{2, 8, 10, 16, 36};
        for (int i = 0; i < radix.length; i++) {
            String str = Integer.toString(100, radix[i]);
            System.out.println(str);
        }
    }
}
