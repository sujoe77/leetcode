# Implementation of a cache

## 1. A simple LRUCache

We have a simple example which is a modified version of example online[1], the code at: 

[Simple LRUCache](../LRUCache.java)

this example illustrated a simple LRU cache implemented with:
1. Deque, which stored cache key by last visited order.
2. HashMap, which stored keys and values

## 1.1 Data structure

## 1.2 Problem
### Reliability
* no timeout, can block for ever

### Performance
* cache penetration

### scalability
* not thread safe
* not scalable



## 2. Multi-thread version


# Ref
1. Simple LRUCache example: https://www.geeksforgeeks.org/lru-cache-implementation/

