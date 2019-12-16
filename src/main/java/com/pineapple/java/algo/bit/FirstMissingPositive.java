package com.pineapple.java.algo.bit;

import java.util.BitSet;

public class FirstMissingPositive {

    public static void main(String[] args) {
        System.out.println(firstMissingPositiveBitMap2(new int[]{1, 2, 0}));
    }

    public int firstMissingPositive(int[] nums) {
        BitSet bitSet = new BitSet(nums.length);
        bitSet.clear();
        for (int i : nums) {
            if (i > 0 && i <= nums.length) {
                bitSet.set(i - 1, true);
            }
        }
        for (int i = 0; i < nums.length; i++) {
            if (!bitSet.get(i)) {
                return i + 1;
            }
        }
        return nums.length + 1;
    }

    public static int firstMissingPositiveBitMap2(int[] nums) {
        int INT_LENGTH = 32;
        int byteLength = (nums.length / INT_LENGTH) + 1;
        int[] bits = new int[byteLength];
        int int1 = 0x0001;
        for (int i : nums) {
            if (i > 0 && i <= nums.length) {
                int index = i / INT_LENGTH;
                int offset = INT_LENGTH - (i % INT_LENGTH);
                bits[index] = bits[index] | int1 << offset;
            }
        }
        for (int i = 1; i < nums.length + 1; i++) {
            if (i > 0 && i <= nums.length) {
                int index = i / INT_LENGTH;
                int offset = INT_LENGTH - (i % INT_LENGTH);
                if ((bits[index] & (int1 << offset)) == 0) {
                    return i;
                }
            }
        }
        return nums.length + 1;
    }
}
