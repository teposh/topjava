package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    List<Meal> getAll();

    Meal get(Integer id);

    void add(Meal meal);

    void update(Meal meal);

    void delete(Integer id);
}
