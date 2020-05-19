package com.pineapple.java.algo.graph;

import java.util.LinkedList;
import java.util.List;

// This class represents a directed graph using adjacency list
// representation
public class Graph {
    private int size; // No. of vertices
    private List<List<Integer>> adj; // Array of lists for Adjacency List Representation

    // Constructor
    public Graph(int size) {
        this.size = size;
        adj = new LinkedList();
        for (int i = 0; i < size; ++i)
            adj.add(new LinkedList());
    }

    //Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj.get(v).add(w); // Add w to v's list.
    }

    public List<List<Integer>> getAdj() {
        return adj;
    }

    public int getSize() {
        return size;
    }
}
