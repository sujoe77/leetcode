package com.pineapple.java.algo.dynamic;

import java.util.HashMap;
import java.util.Map;

/**
 * https://leetcode.com/problems/climbing-stairs/
 */
public class Stairs {
    public static void main(String[] args) {
        map.put(1, 1);
        map.put(2, 2);
        System.out.println(new Stairs().climbStairs(37));
    }

    public static Map<Integer, Integer> map = new HashMap<>();

    public int climbStairs(int n) {
        if (n == 1) {
            return 1;
        } else if (n == 2) {
            return 2;
        }
        return getValue(n -1) + getValue(n-2);
    }

    private int getValue(int n) {
        int a;
        if (map.containsKey(n)) {
            a = map.get(n);
        } else {
            a = climbStairs(n);
            map.put(n, a);
        }
        return a;
    }
}
