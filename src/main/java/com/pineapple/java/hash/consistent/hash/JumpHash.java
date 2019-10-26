package com.pineapple.java.hash.consistent.hash;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * From https://github.com/google/guava/commit/f9318924b71d4ed2c59d3d835fe6a4ce3feefcbf#diff-e6922c1523168e608b83158728d07e63R188
 */
public class JumpHash {

    public static final long CONSTANT = 2862933555777941757L;
    public static final double DOUBLE = 0x1.0p31;

    /**
     * Assigns to {@code input} a "bucket" in the range {@code [0, buckets)}, in a uniform
     * manner that minimizes the need for remapping as {@code buckets} grows. That is,
     * {@code consistentHash(h, n)} equals:
     *
     * <ul>
     * <li>{@code n - 1}, with approximate probability {@code 1/n}
     * <li>{@code consistentHash(h, n - 1)}, otherwise (probability {@code 1 - 1/n})
     * </ul>
     *
     * <p>See the <a href="http://en.wikipedia.org/wiki/Consistent_hashing">wikipedia
     * article on consistent hashing</a> for more information.
     */
    public static int consistentHash(long input, int buckets) {
        checkArgument(buckets > 0, "buckets must be positive: %s", buckets);
        long h = input;
        int candidate = 0;
        int next;

        // Jump from bucket to bucket until we go out of range
        while (true) {
            // See http://en.wikipedia.org/wiki/Linear_congruential_generator
            // These values for a and m come from the C++ version of this function.
            h = CONSTANT * h + 1;
            double inv = DOUBLE / ((int) (h >>> 33) + 1);
            next = (int) ((candidate + 1) * inv);

            System.out.println("next is: " + next);
            if (next >= 0 && next < buckets) {
                candidate = next;
            } else {
                return candidate;
            }
        }
    }
}
