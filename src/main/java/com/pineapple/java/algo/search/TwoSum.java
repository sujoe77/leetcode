package com.pineapple.java.algo.search;

import java.util.HashMap;
import java.util.Map;

public class TwoSum {

    public static void main(String[] args) {
        int[] input = new int[]{3, 2, 4};
        int[] output = twoSumHash(input, 6);
        System.out.println(output[0] + ":" + output[1]);
    }

    public int[] twoSumLoop(int[] nums, int target) {
        for(int i = 0; i<nums.length -1;i++){
            for(int j=i+1;j<nums.length;j++){
                if(nums[i] + nums[j] == target){
                    return new int[]{i,j};
                }
            }
        }
        return new int[]{};
    }

    public static int[] twoSumHash(int[] nums, int target) {
        //map = value, index
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer index = map.get(target - nums[i]);
            if (index != null) {
                return new int[]{i, index};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    //does not work for negative
    public static int[] twoSumBit(int[] nums, int target) {
        int[] map = new int[target];
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > target || nums[i] < 0) {
                continue;
            } else {
                int j = target - nums[i];
                if (map[j] != 0 || (map[j] == 0 && nums[0] == j)) {
                    if (map[j] != i) {
                        return new int[]{map[j], i};
                    }
                }
                map[nums[i]] = i;
            }
        }
        return new int[]{};
    }
}
