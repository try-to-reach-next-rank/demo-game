package com.example.demo.utils;

import java.util.ArrayDeque;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final ArrayDeque<T> pool = new ArrayDeque<>();
    private final Supplier<T> creator;
    private final int maxSize;

    public ObjectPool(Supplier<T> creator, int maxSize) {
        this.creator = creator;
        this.maxSize = maxSize;
    }

    public T acquire() {
        return pool.isEmpty() ? creator.get() : pool.pop();
    }

    public void release(T obj) {
        if (pool.size() < maxSize) {
            pool.push(obj);
        }
    }

    public int size() {
        return pool.size();
    }
}