package com.pineapple.java.sort;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;
import static java.lang.System.arraycopy;

public class MergeSortPrimary {

    public static void main(String[] args) {
        List<Long> source = getSourceList();
        Long[] result = sort(source.toArray(new Long[]{}), 1);
        for (long item : result) {
            System.out.println(item);
        }
    }

    private static List<Long> getSourceList() {
        return Arrays.asList(new Long[]{12L, 11L, 3L, 5L, 19L, 1L, 18L, 0L, 9L, 8L, 20L, 2L, 4L, 6L, 15L});
    }

    private static Long[] sort(Long[] source, int stepSize) {
        if (source.length <= stepSize) {
            return source;
        }
        for (int i = 0; i < source.length; i += 2 * stepSize) {
            int size1 = min(stepSize, source.length - i);
            int size2 = size1 == stepSize ? min(stepSize, source.length - i - stepSize) : 0;
            Long[] temp1 = new Long[size1], temp2 = new Long[size2];
            arraycopy(source, i, temp1, 0, size1);
            arraycopy(source, i + size1, temp2, 0, size2);
            arraycopy(merge(temp1, temp2), 0, source, i, size1 + size2);
        }
        return sort(source, stepSize * 2);
    }

    private static Long[] merge(Long[] list1, Long[] list2) {
        if (list1 == null && list2 == null) {
            return new Long[]{};
        } else if (list1 == null || list1.length == 0) {
            return list2;
        } else if (list2 == null || list2.length == 0) {
            return list1;
        }
        Long[] ret = new Long[list1.length + list2.length];
        int i = 0, j = 0, k = 0;
        while (i < list1.length || j < list2.length) {
            long item = i == list1.length ? list2[j]
                    : j == list2.length ? list1[i]
                    : min(list1[i], list2[j]);
            ret[k++] = item;
            if (i < list1.length && item == list1[i]) {
                i++;
            } else {
                j++;
            }
        }
        return ret;
    }
}
