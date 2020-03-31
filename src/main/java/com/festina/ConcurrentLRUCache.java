package com.festina;

/**
 * This class inspired by an example in Chapter 5.6 in "Java Concurrency in Practice" by Brian Goetz
 */

import java.util.concurrent.*;
import java.util.function.Function;

public class ConcurrentLRUCache<A, V> {
    private final int MAX_SIZE = 10;

    private final LinkedBlockingDeque<A> keyQueue = new LinkedBlockingDeque();
    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();

    private final Function<A, V> function;

    public ConcurrentLRUCache(Function<A, V> computable) {
        this.function = computable;
    }

    public V get(final A key) {
        return get(key, function);
    }

    public V get(final A key, Function<A, V> fun) {
        return process(key, fun);
    }

    /**
     * Actually, this can be replaced by computeIfAbsent in ConcurrentHashMap
     *
     */
    private V process(final A key, Function<A, V> function) {
        while (true) {
            Future<V> f = cache.get(key);
            if (f == null) {
                FutureTask<V> ft = new FutureTask<>(() -> function.apply(key));
                f = cache.putIfAbsent(key, ft);
                if (f == null) {
                    f = ft;
                    ft.run();
                }
            } else {
                keyQueue.removeLastOccurrence(key);
            }

            try {
                V ret = f.get();
                keyQueue.push(key);
                if (keyQueue.size() > MAX_SIZE) {
                    A lastKey = keyQueue.removeLast();
                    cache.remove(lastKey);
                }
                return ret;
            } catch (CancellationException e) {
                cache.remove(key, f);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
