package com.pineapple.java.algo.graph;

public class DFSVisitor implements Visitor {
    private boolean visited[];

    public DFSVisitor(Graph g) {
        this.visited = new boolean[g.getSize()];
    }

    @Override
    public void visit(Graph g, int v) {
        // Mark the current node as visited and print it
        visited[v] = true;
        System.out.print(v + " ");

        // Recur for all the vertices adjacent to this vertex
        for(Integer n : g.getAdj().get(v)){
            if (!visited[n]) {
                visit(g, n);
            }
        }
    }
}
