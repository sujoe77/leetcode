package com.pineapple.java.algo.sort;

import java.util.Arrays;

//Java 代码实现
public class HeapSort implements IArraySort {

    @Override
    public int[] sort(int[] sourceArray) throws Exception {
        // 对 arr 进行拷贝，不改变参数内容
        int[] arr = Arrays.copyOf(sourceArray, sourceArray.length);

        int len = arr.length;

        buildMaxHeap(arr, len);

        printArray(arr);

        for (int i = len - 1; i > 0; i--) {
            System.out.println("len is: " + len + " ----------");
            swap(arr, 0, i);
            printArray(arr);
            len--;
            heapify(arr, 0, len);
        }
        return arr;
    }

    private void printArray(int[] arr) {
        for (int i : arr)
            System.out.print(i + ",");
        System.out.println("");
    }

    private void buildMaxHeap(int[] arr, int len) {
        for (int i = (int) Math.floor(len / 2); i >= 0; i--) {
            heapify(arr, i, len);
        }
    }

    private void heapify(int[] arr, int i, int len) {
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;

        if (left < len && arr[left] > arr[largest]) {
            largest = left;
        }

        if (right < len && arr[right] > arr[largest]) {
            largest = right;
        }

        if (largest != i) {
            swap(arr, i, largest);
            printArray(arr);
            heapify(arr, largest, len);
        }
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

}

