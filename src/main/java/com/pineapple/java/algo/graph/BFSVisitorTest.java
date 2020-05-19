package com.pineapple.java.algo.graph;

import org.testng.annotations.Test;

public class BFSVisitorTest {
    @Test
    public void test(){
        Graph g = new Graph(4){{
            addEdge(0, 1);
            addEdge(0, 2);
            addEdge(1, 2);
            addEdge(2, 0);
            addEdge(2, 3);
            addEdge(3, 3);
        }};

        System.out.println("Following is Depth First Traversal (starting from vertex 2)");

        new BFSVisitor(g).visit(g, 2);
    }
}
