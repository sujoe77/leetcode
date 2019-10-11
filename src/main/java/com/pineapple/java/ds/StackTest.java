package com.pineapple.java.ds;

import org.testng.annotations.Test;

import java.util.Stack;

public class StackTest {
    @Test
    public void testBracket() {
        String input = "asd{\nfd{dsd}sds}sdsd}{sdsd}";
        char[] chars = input.toCharArray();
        Stack<String> stack = new Stack();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '{') {
                stack.push("{" + i);
            } else if (chars[i] == '}') {
                if (!stack.isEmpty() && stack.peek().startsWith("{")) {
                    stack.pop();
                } else {
                    stack.push("}" + i);
                }
            }
        }
        while (!stack.isEmpty()) {
            System.out.println(stack.pop());
        }
    }
}
