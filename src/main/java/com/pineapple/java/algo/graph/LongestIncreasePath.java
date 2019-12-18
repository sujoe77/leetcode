package com.pineapple.java.algo.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * https://leetcode.com/problems/longest-increasing-path-in-a-matrix/
 * https://www.geeksforgeeks.org/topological-sorting/
 * topological sorting
 */
public class LongestIncreasePath {

    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {9, 9, 4},
                {6, 6, 8},
                {2, 1, 1}
        };
        System.out.println(getLongest(matrix));
    }

    private static int getLongest(int[][] matrix) {
        Node[][] nodes = Node.fromPrimary(matrix);
        System.out.println(nodes);
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j].initDowStream(nodes);
            }
        }
        System.out.println(nodes);
        Stack<Node> stack = topologicalSorting(nodes);
        List<Node> longest = new ArrayList<>();
        List<Node> temp = new ArrayList<>();
        for (int i = 0; i < stack.size(); i++) {
            Node node = stack.get(stack.size() - i - 1);
            visit(temp, node, longest);
            temp.clear();
        }
        return longest.size();
    }

    private static Stack<Node> topologicalSorting(Node[][] nodes) {
        Stack<Node> stack = new Stack<>();
        while (stack.size() < nodes.length * nodes[0].length) {
            for (int i = 0; i < nodes.length; i++) {
                for (int j = 0; j < nodes[0].length; j++) {
                    Node node = nodes[i][j];
                    if (node.downStreamNodes.isEmpty() && !stack.contains(node)) {
                        System.out.println("empty downstream, pushing: " + node);
                        stack.push(node);
                    } else if (stack.containsAll(node.downStreamNodes) && !stack.contains(node)) {
                        System.out.println("all downstream in, pushing: " + node);
                        stack.push(node);
                    } else {
                        System.out.println("skip: " + node);
                    }
                }
            }
        }
        System.out.println(stack);
        return stack;
    }

    private static void visit(List<Node> temp, Node node1, List<Node> longest) {
        temp.add(node1);
        if (node1.downStreamNodes.isEmpty()) {
            if (longest.size() < temp.size()) {
                longest.clear();
                for (Node node : temp) {
                    longest.add(node);
                }
            }
        } else {
            for (Node node2 : node1.downStreamNodes) {
                visit(temp, node2, longest);
                temp.remove(temp.get(temp.size() - 1));
            }
        }
    }

    public int longestIncreasingPath(int[][] matrix) {
        if (matrix.length == 0) {
            return 0;
        }
        int width = matrix.length;
        int length = matrix[0].length;
        int totalLength = width * length;
        int maxLength = 1;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                int[][] visited = new int[width][length];
            }
        }
        return maxLength;
    }
}

