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
- HashMap (buckets + linked List of Nodes)

see also

- LinkedHashMap, TreeMap

### 1.2 Problem

#### Reliability

- no timeout, can block for ever

#### Performance

- async?
- cache penetration

#### Scalability

- not thread safe
  - ConcurrentModificationException
  - infinite loop on next()
- not scalable

## 2. Multi-thread version

A multi-thread version from "Java Concurrency in Practice" (Ref.[2])

[ConcurrentLRUCache.java](./ConcurrentLRUCache.java)

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
- visibility and atomicity
  - compare-and-swap, volatile, Atomic, ThreadLocal
- Future
- happens-before

3 groups of Collections

- Not synchronized
- Synchronized

  - vector, hashtable, Collections.synchronizedXxx(), etc
  - synchronized on method

- Concurrent (java.util.concurrent)
  - synchronized vs concurrent
    - performance, scalability
    - lock striping (hashTable vs ConcurrentHashMap)

Future

- timeout
- avoid busy waiting
- life cycle management

Future implementation

    /** The underlying callable; nulled out after running */
    private Callable<V> callable;
    /** The result to return or exception to throw from get() */
    private Object outcome; // non-volatile, protected by state reads/writes
    /** The thread running the callable; CASed during run() */
    private volatile Thread runner;
    /** Treiber stack of waiting threads */
    private volatile WaitNode waiters;

# 3. Distributed cache

hosting

- Dedicated cache cluster
- co-located cache

configuration service

Memcached / Redis / GemFire are examples of distributed cache

## 3.1 partition

Consistent Hashing (Ref.[6])

- how it works
- problem and solutions
  - evenly distribute load -> "Bounded Load" Ref[9]
  - rebalance -> SPOCA Ref[8], virtual nodes

## 3.2 replication

3 ways of replication (Ref. [5])

- single leader
- multiple leader
- leader less (dynamo style)

## 3.3 Event based

## 3.4 Use cases

## 3.5 problem and solutions

- cache avalanche
- penetration (default value, bloomfilter Ref[10])
- concurrency (single thread)
  - 7 models of concurrency

Ref[7]

## 3.5 Zookeeper

Use cases

- leader election
- configuration service
- distributed transactions
- total order
- etc

Ref[3]

# 4. Some problem we met in real

- HashMap infinite loop -> concurrent hashmap
- Idempotence (duplicated transfer caused by john in cpg) -> use one thread / unique id
- copyOnWrite -> make a copy
- field with thread id -> avoid using threadid
- ThreadLocal, atomic, volatile
- retry pattern -> count down latches, random interval
- overwrite record -> optimistic locking
- replication problem -> aof
- no timeout on time consuming calls -> Future

- solution
  - optimistic locking
  - immutable, function programming, share nothing (confine to 1 thread, nonblocking and lock-free)

# Ref

1. Simple LRUCache example: https://www.geeksforgeeks.org/lru-cache-implementation/
2. Java Concurrency in Practice: https://www.amazon.com/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601
3. ZooKeeper: Wait-free coordination for Internet-scale systems https://www.usenix.org/legacy/event/atc10/tech/full_papers/Hunt.pdf
4. Redis in Action: https://www.manning.com/books/redis-in-action
5. Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems https://www.amazon.com/Designing-Data-Intensive-Applications-Reliable-Maintainable/dp/1449373321
6. David Karger, Eric Lehman, Tom Leighton, et al.: “Consistent Hashing and Random Trees: Distributed Caching Protocols for Relieving Hot Spots on the World Wide Web,” at 29th Annual ACM Symposium on Theory of Computing (STOC), pages 654–663, 1997. doi:10.1145/258533.258660 http://www.akamai.com/dl/technical_publications/ConsistenHashingandRandomTreesDistributedCachingprotocolsforrelievingHotSpotsontheworldwideweb.pdf
7. Redis problems. http://appdianping.com/2019/03/27/how-to-solve-the-five-difficulties-of-redis-avalanche-penetration-and-concurrent/
8. SPOCA: https://www.usenix.org/legacy/event/atc11/tech/final_files/Chawla.pdf
9. Bounded load: https://arxiv.org/abs/1608.01350
10. Space/time trade-offs in hash coding with allowable errors: https://dl.acm.org/doi/10.1145/362686.362692
