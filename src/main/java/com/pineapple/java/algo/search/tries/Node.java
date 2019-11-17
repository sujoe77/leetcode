package com.pineapple.java.algo.search.tries;

import java.util.*;

public class Node {
    public static final Node END_NODE = new Node((char) 0x0000);
    private final char value;
    private final Map<Character, Node> nextCharSet = new HashMap();

    public Node(char value) {
        this.value = value;
    }

    public void append(Node node){
        nextCharSet.put(node.getValue(), node);
    }

    public char getValue() {
        return value;
    }

    public Map<Character, Node> getNextCharSet() {
        return nextCharSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value == node.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
