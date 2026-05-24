package com.gcvisualization.functional;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    void execute(T t);
}
