package com.pineapple.java.algo.hash.consistent;

import com.pineapple.java.algo.hash.consistent.hash.HashFunction;
import com.pineapple.java.algo.hash.consistent.hash.SimpleHashFunction;

import java.util.SortedMap;
import java.util.TreeMap;

public class LoadBalancer {
    // Sorted Map.
    private final static SortedMap<Integer, String> BUCKET_ID_TO_SERVER = new TreeMap<>();
    private static final int NUM_SERVER = 10;
    static LoadBalancer INSTANCE = new LoadBalancer(new SimpleHashFunction(61, 59));

    private final HashFunction hashFunction;

    private LoadBalancer(final HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    public String findServer(int randomUserId) {
        if (BUCKET_ID_TO_SERVER.isEmpty()) {
            setUpServer(hashFunction, NUM_SERVER);
        }
        final int userIdHash = toUserIdHash(randomUserId);
        Integer bucketId = getBucketId(userIdHash);
        final String server = BUCKET_ID_TO_SERVER.get(bucketId);

        System.out.println("--------------------------------------------------------------------------");
        System.out.println(String.format("User ID : %d, Hash: %d, Server: %s", randomUserId, userIdHash, server));
        return server;
    }

    private void setUpServer(HashFunction hashFunction, int numServer) {
        // Setup the servers
        for (int i = 1; i <= numServer; i++) {
            final String server = "Server : " + i;
            // Can be situation of hash collision, which would override the previous server. Else again hash with some other function.
            int hashValue = hashFunction.getHashValue(server);
            hashValue = i * (hashFunction.getLimit() / numServer);
            BUCKET_ID_TO_SERVER.put(hashValue, server);
            System.out.println(String.format("add mapping: %d -> %s", hashValue, server));
        }
    }

    private int toUserIdHash(int randomUserId) {
        return hashFunction.getHashValue(randomUserId);
    }

    private Integer getBucketId(int userIdHash) {
        final SortedMap<Integer, String> tailMap = BUCKET_ID_TO_SERVER.tailMap(userIdHash);
        return (!tailMap.isEmpty() ? tailMap : BUCKET_ID_TO_SERVER).firstKey();
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }
}
