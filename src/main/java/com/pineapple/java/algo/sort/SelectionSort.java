package com.pineapple.java.algo.sort;

import java.util.Arrays;

import static com.pineapple.java.algo.sort.SortUtil.swap;

//Java 代码实现
public class SelectionSort implements IArraySort {

    @Override
    public int[] sort(int[] sourceArray) throws Exception {
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        // 总共要经过 N-1 轮比较
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            min = findMin(arr, i);

            // 将找到的最小值和i位置所在的值进行交换
            if (i != min) {
                swap(arr, i, min);
            }

        }
        return arr;
    }

    private int findMin(int[] arr, int i) {
        // 每轮需要比较的次数 N-i
        int min = i;
        for (int j = i + 1; j < arr.length; j++) {
            if (arr[j] < arr[i]) {
                // 记录目前能找到的最小值元素的下标
                min = j;
            }
        }
        return min;
    }
}

