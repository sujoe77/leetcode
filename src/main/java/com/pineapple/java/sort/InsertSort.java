package com.pineapple.java.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertSort {

    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.addAll(Arrays.asList(new Integer[]{3, 5, 2, 1, 4, 9, 8, 0}));
        for (int i = 1; i < numbers.size(); i++) {
            sort(numbers, i);
        }
        for(int i=0;i<numbers.size();i++){
            System.out.println(numbers.get(i));
        }
    }

    private static void sort(List<Integer> numbers, int index) {
        int i = index;
        while (i > 0 && numbers.get(i - 1) > numbers.get(i)) {
            int temp = numbers.get(i);
            numbers.set(i, numbers.get(i - 1));
            numbers.set(i - 1, temp);
            i--;
        }
    }

}
