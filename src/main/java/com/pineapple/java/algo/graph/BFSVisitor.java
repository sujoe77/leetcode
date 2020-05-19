package com.pineapple.java.algo.graph;

public class BFSVisitor implements Visitor {
    private boolean visited[];

    public BFSVisitor(Graph g) {
        this.visited = new boolean[g.getSize()];
    }

    //method 1, with queue
    //visit node, add left and right to queue
    //repeat 2 on left, and right

    @Override
    public void visit(Graph g, int v) {
        // Mark the current node as visited and print it
        if (!visited[v]) {
            visited[v] = true;
            System.out.print(v + " ");
        }

        // Recur for all the vertices adjacent to this vertex
        boolean done = true;
        for (Integer n : g.getAdj().get(v)) {
            if (!visited[n]) {
                done = false;
                visited[n] = true;
                System.out.print(n + " ");
            }
        }

        if (done) {
            return;
        }

        for (Integer n : g.getAdj().get(v)) {
//            if (!visited[n]) {
            visit(g, n);
//            }
        }
    }
}
