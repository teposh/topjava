package ru.javawebinar.topjava.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdProvider {
    private final static AtomicInteger counter = new AtomicInteger();

    public static Integer nextId() {
        return counter.getAndIncrement();
    }
}
