package ru.javawebinar.topjava.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdProvider {
    private static final AtomicInteger counter = new AtomicInteger();

    public static Integer nextId() {
        return counter.getAndIncrement();
    }
}
