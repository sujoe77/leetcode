package com.pineapple.java.algo.search;

public class WaterContainer {
    public static void main(String[] args) {
        int[] height = new int[]{159, 157, 139, 51, 98, 71, 4, 125, 48, 125, 64, 4, 105, 79, 136, 169, 113, 13, 95, 88, 190, 5, 148, 17, 152, 20, 196, 141, 35, 42, 188, 147, 199, 127, 198, 49, 150, 154, 175, 199, 80, 191, 3, 137, 22, 92, 58, 87, 57, 153, 175, 199, 110, 75, 16, 62, 96, 12, 3, 83, 55, 144, 30, 6, 23, 28, 56, 174, 183, 183, 173, 15, 126, 128, 104, 148, 172, 163, 35, 181, 68, 162, 181, 179, 37, 197, 193, 85, 10, 197, 169, 17, 141, 199, 175, 164, 180, 183, 90, 115};
        height = new int[]{1, 2, 4, 3};
        System.out.println(new WaterContainer().maxArea(height));
    }

    public int maxArea(int[] height) {
        int i = 0, j = height.length - 1;
        int maxSize = getSize(height, i, j);
        while (j - i > 0) {
            int newSize = getSize(height, i, j);
            if (newSize > maxSize) {
                maxSize = newSize;
            }
            // int change = height[i + 1] - height[i] - height[j - 1] + height[j];
            int diff = height[i] - height[j];
            if (diff < 0 || diff == 0 && height[i + 1] - height[i] - height[j - 1] + height[j] > 0) {
                i++;
            } else {
                j--;
            }
        }
        return maxSize;
    }

    public int getSize(int[] height, int i, int j) {
        return (height[i] < height[j] ? height[i] : height[j]) * (j - i);
    }
}
