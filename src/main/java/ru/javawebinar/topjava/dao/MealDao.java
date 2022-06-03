package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getAll();

    Meal get(int id);

    Meal add(Meal meal);

    Meal update(Meal meal);

    void delete(int id);
}
