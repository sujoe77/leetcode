package com.pineapple.java.hash.consistent.hash;

public interface HashFunction {
    int getHashValue(int randomUserId);

    int getHashValue(String word);

    int getLimit();
}
