package com.pineapple.java.algo.search;

import java.util.ArrayList;
import java.util.List;

public class Medium2Arrays {

    public static void main(String[] args) {
        int i1[] = new int[]{1};
        int i2[] = new int[]{};
        System.out.println(findMedium(i1, i2));
    }

    public static double findMedium(int[] nums1, int[] nums2) {
        int i = 0, j = 0;
        int totalSize = nums1.length + nums2.length;
        List<Integer> totalList = new ArrayList<>();
        boolean even = totalSize / 2 == totalSize / 2.0;
        int half = even ? totalSize / 2 : (totalSize / 2) + 1;
        while (totalList.size() < totalSize && totalList.size() < half + 1 && totalSize > 0) {
            int index = findMin(nums1, nums2, i, j);
            totalList.add(index == 0 ? nums1[i++] : nums2[j++]);
        }
        return even ? (totalList.get(half) + totalList.get(half - 1)) / 2.0 : totalList.get(half - 1);
    }

    private static int findMin(int[] nums1, int[] nums2, int index1, int index2) {
        boolean noMoreNums1 = index1 > nums1.length - 1;
        boolean noMoreNums2 = index2 > nums2.length - 1;
        if (noMoreNums1 && noMoreNums2) {
            return -1;
        } else if (noMoreNums1 || (!noMoreNums2 && nums1[index1] >= nums2[index2])) {
            return 1;
        } else if (noMoreNums2 || (!noMoreNums1 && nums1[index1] <= nums2[index2])) {
            return 0;
        }
        return -1;
    }
}


