package com.pineapple.java.algo.sort;

public class SortUtil {
    public static void swap(int[] arr, int j, int k) {
        int tmp = arr[j];
        arr[j] = arr[k];
        arr[k] = tmp;
    }
}
