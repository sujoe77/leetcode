package com.pineapple.java.algo.string;

public class MinWindow {

    public static void main(String[] args) {
        String S = "ADOBECODEBANC", T = "ABC";
    }

    public String minWindow(String s, String t) {
        int tLen = t.length();
        int x = 0, y = -1;
        if (s.length() < tLen) {
            return "";
        }
        char[] chars = s.toCharArray();

        while (x < s.length() && y < s.length()) {
            if(y == -1) {
                if (t.indexOf(chars[x]) >= 0) {
                    y = x + tLen;
                } else {
                    x++;
                }
            } else if()
        }
    }
}
