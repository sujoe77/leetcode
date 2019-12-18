package com.pineapple.java.algo.math;

/**
 * https://leetcode.com/problems/sqrtx/submissions/
 */
public class Sqrt {
    public static void main(String[] args) {
        new Sqrt().mySqrt(37);
    }

    public int mySqrt(int x) {
        //newton
        double tempY = 16;
        double tempX = 4;
        int tempXold = -1;
        while (tempXold != (int)tempX) {
            tempXold = (int) tempX;
            tempX = tempX -  (tempY - x) / (tempX * 2.0);
            if(tempXold == (int) tempX){
                break;
            }
            tempY = tempX * tempX;
        }
        return tempXold;
    }
}
