package com.pineapple.java.hash.consistent.hash;

public class SimpleHashFunction implements HashFunction {
    // Consistent Hashing with Ring having 50 buckets.
    private final static int LIMIT = 50;

    private long prime;
    private long odd;

    public SimpleHashFunction(final long prime, final long odd) {
        this.prime = prime;
        this.odd = odd;
    }

    @Override
    public int getHashValue(int randomUserId) {
        return getHashValue(String.valueOf(randomUserId));
    }

    @Override
    public int getHashValue(final String word) {
        int hash = word.hashCode();
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return calculateHash(hash, prime, odd);
    }

    @Override
    public int getLimit() {
        return LIMIT;
    }

    private int calculateHash(final int hash, final long prime, final long odd) {
        return (int) ((((hash % LIMIT) * prime) % LIMIT) * odd) % LIMIT;
    }


}
