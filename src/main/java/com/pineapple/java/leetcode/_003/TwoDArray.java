package com.pineapple.java.leetcode._003;

import org.apache.commons.lang3.StringUtils;

import static java.lang.Math.abs;

public class TwoDArray {
    public static final int SIZE = 10;
    public static int[][] array = new int[10][10];

    public static void main(String[] args) {
        initArray();
        System.out.println(getPos(23));
    }

    public static void initArray() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                array[i][j] = 2 * i + 2 * j;
                System.out.print(String.format("%02d", 2 * i + 2 * j) + ",");
            }
            System.out.print("\n");
        }
    }

    public static String getPos(int n) {
        if (array[SIZE - 1][SIZE - 1] < n || array[0][0] > n) {
            return "not found!!";
        }

        int iStep = SIZE / 2, jStep = SIZE / 2;
        int i = 0, j = 0;
        String result = "";
        while (iStep != 0 || jStep != 0) {
            System.out.println(String.format("%d,%d", i, j));
            i += iStep;
            j += jStep;
            System.out.println(String.format("After move %d,%d", i, j));
            result = check9Cells(n, i, j);
            if (StringUtils.isNotBlank(result)) {
                return result;
            }

            int absIStep = abs(iStep);
            if (i - 1 >= 0 && array[i - 1][j] > n) {
                iStep = absIStep == 1 ? -1 : -1 * absIStep / 2;
                System.out.println("case 1, iStep is: " + iStep);
            } else if (i + 1 < SIZE && array[i + 1][j] < n) {
                iStep = absIStep == 1 ? 1 : absIStep / 2;
                System.out.println("case 2, iStep is: " + iStep);
            }

            int absJStep = abs(jStep);
            if (j - 1 >= 0 && array[i][j - 1] > n) {
                jStep = absJStep == 1 ? -1 : -1 * absJStep / 2;
                System.out.println("case 3, jStep is: " + jStep);
            } else if (j + 1 < SIZE && array[i][j + 1] < n) {
                jStep = absJStep == 1 ? 1 : 1 * absJStep / 2;
                System.out.println("case 4, jStep is: " + jStep);
            }
        }
        return String.format("not found!!");
    }

    private static String check9Cells(int n, int i, int j) {
        int min = array[i > 0 ? i - 1 : 0][j > 0 ? j - 1 : j];
        int max = array[i > SIZE - 2 ? SIZE - 1 : i + 1][j > SIZE - 2 ? SIZE - 1 : j + 1];

        //n in not in current 9 cells
        if(n < min || n > max){
            return "";
        }

        for (int ii = -1; ii < 2; ii++) {
            for (int jj = -1; jj < 2; jj++) {
                if (i + ii < SIZE && i + ii >= 0 && j + jj < SIZE && j + jj >= 0 && array[i + ii][j + jj] == n) {
                    return String.format("%d, %d", i + ii, j + jj); //found n
                }
            }
        }

        //n should be in 9 cells, but not found
        return "not found!";
    }
}
