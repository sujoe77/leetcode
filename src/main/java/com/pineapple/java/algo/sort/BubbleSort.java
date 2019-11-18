package com.pineapple.java.algo.sort;

import java.util.Arrays;

// Java 代码实现
public class BubbleSort implements IArraySort {


    @Override
    public int[] sort(int[] sourceArray) throws Exception {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        for (int i = 1; i < arr.length; i++) {
            // 设定一个标记，若为true，则表示此次循环没有进行交换，也就是待排序列已经有序，排序已经完成。
            boolean flag = true;

            for (int j = 0; j < arr.length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    SortUtil.swap(arr, j, j + 1);
                    flag = false;
                }
            }

            if (flag) {
                break;
            }
        }
        return arr;
    }

}
