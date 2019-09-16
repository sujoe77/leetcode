package com.pineapple.java.gof23.visitor;

public interface Subject {
    void accept(Visitor visitor);
    String getSubject();
}
