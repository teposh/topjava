package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealDao implements MealDao {
    private final Map<Integer, Meal> meals = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger();

    public InMemoryMealDao() {
        Arrays.asList(
                new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        ).forEach(this::add);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(meals.values());
    }

    @Override
    public Meal get(int id) {
        return meals.get(id);
    }

    @Override
    public Meal add(Meal meal) {
        return meals.computeIfAbsent(counter.getAndIncrement(), (k) -> new Meal(k, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    @Override
    public Meal update(Meal meal) {
        return meals.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public void delete(int id) {
        meals.remove(id);
    }
}
