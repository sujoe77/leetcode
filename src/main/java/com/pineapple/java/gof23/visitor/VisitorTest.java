package com.pineapple.java.gof23.visitor;

public class VisitorTest {
    public static void main(String[] args) {
        Visitor visitor = new MyVisitor();
        Subject sub = new MySubject();
        sub.accept(visitor);
    }
}

