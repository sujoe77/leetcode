package com.pineapple.java.sdk;

import org.testng.annotations.Test;

import static java.lang.Math.abs;

public class MathTest {
    @Test
    public void testRound() {
        float[] v = new float[]{365f / 88f, 365f / 224.7f, 1f, 1 / 1.8809f, 1 / 11.8618f,
                365f / 10759f, 365f / 30688.5f, 365f / 60182f};
        for (float i = 0; i < 100; i += 1 / 12.0f) {
            float tTotal = i;
            float absV = 0;
            if(i % 120 < 1){
                System.out.println("i is: " + i);
            }
            int maxSize = 2;
            for (int j = 0; j < maxSize - 1; j++) {
                for (int k = j + 1; k < maxSize; k++) {
                    float deta1 = (tTotal * (v[j] - v[k])) * 360 % 360;
                    absV += abs(deta1);
                }
            }

            if (absV < 10 && i > 0) {
                System.out.println(tTotal + ":");
                for (int l = 0; l < v.length; l++) {
                    System.out.print(l + "->" + v[l] * tTotal * 360 % 360);
                    System.out.println("------------");
                }
            }
        }
    }
}
