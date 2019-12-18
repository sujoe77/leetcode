package com.pineapple.java.algo.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Node {
    final int i;
    final int j;
    final int value;
    final Collection<Node> downStreamNodes = new ArrayList<>();

    public static Node[][] fromPrimary(int[][] matrix) {
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

    public void initDowStream(Node[][] nodes) {
        List<Node> neighbors = getNeighbors(nodes, i, j);
        for (Node node : neighbors) {
            if (isSubStreamNode(this, node)) {
                downStreamNodes.add(node);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%d,%d,%d", i, j, value);
    }

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
