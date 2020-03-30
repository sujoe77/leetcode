# Implementation of a cache

## Goals

Functional requirement

- put and get with key
- multiple data structures

Nonfunctional

- Availability
- Consistency
  - transaction
  - replication
- Performance
  - throughput
  - latency
- Scalability
- Resilience

## 1. A simple LRUCache

We have a simple example which is a modified version of example online[1], the code at:

[Simple LRUCache](./LRUCache.java)

this example illustrated a simple LRU cache implemented with:

1. Deque, which stored cache key by last visited order.
2. HashMap, which stored keys and values

### 1.1 Data structure

- Deque
- HashMap, how it works

### 1.2 Problem

#### Reliability

- no timeout, can block for ever

#### Performance

- async?
- cache penetration

#### scalability

- not thread safe
  - ConcurrentModificationException
  - infinite loop on next()
- not scalable

## 2. Multi-thread version

A multi-thread version from "Java Concurrency in Practice" (Ref.[2])

### 2.1 Improvement

- thread safe collections, putIfAbsent
- Future for async (smaller inconsistent window, timeout, cancellation)
- single thread performance vs scalability

Limitations

- no timeout (one of the problem of distributed system)
- not scalable to multiple host

### 2.2 Implementation

Thread safe

- concurrent collections
- compare-and-swap
- Future
- happens-before
- visibility and atomicity

3 groups of Collections

- Not synchronized
- Synchronized (vector, hashtable, Collections.synchronizedXxx(), etc)
- Concurrent (java.util.concurrent)
  - synchronized vs concurrent
    - performance, scalability
    - synchronized vs lock striping (hashTable vs ConcurrentHashMap)

Future

- timeout
- avoid busy waiting
- life cycle management

### problem

# 3. Distributed cache

hosting

- Dedicated cache cluster
- co-located cache

configuration service

Memcached / Redis are examples of distributed cache

## 3.1 partition

Consistent Hashing (Ref.[6])

- how it works
- problem and solutions
  - evenly distribute load
  - rebalance

## 3.2 replication

3 ways of replication (Ref. [5])

- single leader
- multiple leader
- leader less (dynamo style)

## 3.3 Event based

## 3.4 Use cases

## 3.5 problem and solutions

Ref[7]

## 3.5 Zookeeper

Use cases

- leader election
- configuration service
- distributed transactions
- total order
- etc

Ref[3]

# Ref

1. Simple LRUCache example: https://www.geeksforgeeks.org/lru-cache-implementation/
2. Java Concurrency in Practice: https://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601
3. ZooKeeper: Wait-free coordination for Internet-scale systems https://www.usenix.org/legacy/event/atc10/tech/full_papers/Hunt.pdf
4. Redis:
5. Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems https://www.amazon.com/Designing-Data-Intensive-Applications-Reliable-Maintainable/dp/1449373321
6. David Karger, Eric Lehman, Tom Leighton, et al.: “Consistent Hashing and Random Trees: Distributed Caching Protocols for Relieving Hot Spots on the World Wide Web,” at 29th Annual ACM Symposium on Theory of Computing (STOC), pages 654–663, 1997. doi:10.1145/258533.258660 http://www.akamai.com/dl/technical_publications/ConsistenHashingandRandomTreesDistributedCachingprotocolsforrelievingHotSpotsontheworldwideweb.pdf
7. Redis problems. http://appdianping.com/2019/03/27/how-to-solve-the-five-difficulties-of-redis-avalanche-penetration-and-concurrent/
8. Redis in action https://redislabs.com/redis-in-action/
