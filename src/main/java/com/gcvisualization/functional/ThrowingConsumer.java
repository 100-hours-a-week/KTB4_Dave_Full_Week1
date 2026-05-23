package com.gcvisualization.functional;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    public void execute(T t);
}
