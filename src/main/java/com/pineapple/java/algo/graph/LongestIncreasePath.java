package com.pineapple.java.algo.graph;

import java.util.*;

public class LongestIncreasePath {

    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {9, 9, 4},
                {6, 6, 8},
                {2, 1, 1}
        };
        Node[][] nodes = Node.toObjects(matrix);
        System.out.println(nodes);
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j].initDowStream(nodes);
            }
        }
        System.out.println(nodes);
        Stack<Node> stack = new Stack<>();
        while (stack.size() < matrix.length * matrix[0].length) {
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
        List<Node> longest = new ArrayList<>();
        List<Node> temp = new ArrayList<>();
        for (int i = 0; i < stack.size(); i++) {
            Node node = stack.get(stack.size() - i - 1);
            visit(temp, node, longest);
            temp.clear();
        }
        System.out.println(longest);
    }

    private static void visit(List<Node> temp, Node node1, List<Node> longest) {
        temp.add(node1);
        if (node1.downStreamNodes.isEmpty()) {
            if (longest.size() < temp.size()) {
                longest.clear();
                for(Node node : temp){
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

    //topological sorting
//https://www.geeksforgeeks.org/topological-sorting/
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

    public List<Node> getTopogicalSorting(int[][] matrix) {
        List<Node> ret = null;

        return ret;
    }

    private static class Node {
        final int i;
        final int j;
        final int value;
        final Collection<Node> downStreamNodes = new ArrayList<>();

        public static Node[][] toObjects(int[][] matrix) {
            Node[][] ret = new Node[matrix.length][matrix[0].length];
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[0].length; j++) {
                    ret[i][j] = new Node(i, j, matrix[i][j]);
                }
            }
            return ret;
        }

        public static List<Node> getNeighbors(Node[][] nodes, int i, int j) {
            List<Node> ret = new ArrayList<>();
            int[] array = {-1, 0, 1};
            for (int i1 : array) {
                for (int j1 : array) {
                    int indexI = i + i1;
                    int indexJ = j + j1;
                    if (indexI >= 0 && indexI < nodes.length && indexJ >= 0 && indexJ < nodes[0].length && ((i1 * j1 == 0) && (i1 + j1) != 0)) {
                        ret.add(nodes[indexI][indexJ]);
                    }
                }
            }
            return ret;
        }

        public static boolean isSubStreamNode(Node local, Node input) {
            return local.getValue() < input.getValue() &&
                    ((Math.abs(input.getI() - local.getI()) == 1 && input.getJ() == local.getJ()) || (Math.abs(input.getJ() - local.getJ()) == 1 && input.getI() == local.getI()));
        }

        public Node(int i, int j, int value) {
            this.i = i;
            this.j = j;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", i, j, value);
        }

        //        public Node(int i, int j, Node[][] matrix) {
//            this.i = i;
//            this.j = j;
//            this.value = matrix[i][j].getValue();
//            initDowStream(i, j, matrix);
//        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return i == node.i &&
                    j == node.j &&
                    value == node.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j, value);
        }

        private void initDowStream(Node[][] nodes) {
            List<Node> neighbors = getNeighbors(nodes, i, j);
            for (Node node : neighbors) {
                if (isSubStreamNode(this, node)) {
                    downStreamNodes.add(node);
                }
            }
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public int getValue() {
            return value;
        }
    }
}

