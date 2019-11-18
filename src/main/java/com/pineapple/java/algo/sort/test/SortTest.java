package com.pineapple.java.algo.sort.test;

import com.pineapple.java.algo.sort.BubbleSort;
import com.pineapple.java.algo.sort.IArraySort;
import com.pineapple.java.algo.sort.SelectionSort;
import org.testng.annotations.Test;

public class SortTest {
    @Test
    public void testSort() throws Exception {
//        doSort(new BubbleSort());
        doSort(new SelectionSort());
    }

    private void doSort(IArraySort sortClass) throws Exception {
        int[] array = {1, 3, 5, 2, 10, 8, 7, 4, 0, 6};
        int[] ret = sortClass.sort(array);
        for (int i : ret) {
            System.out.println(i);
        }
    }
}
