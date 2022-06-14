package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<Meal> COMPARATOR = Comparator.comparing(Meal::getDateTime);

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(m -> this.save(m, 1));
        MealsUtil.meals2.forEach(m -> this.save(m, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        Map<Integer, Meal> userMeals = repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return false;
        return userMeals.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return null;
        return userMeals.get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllFilteredByDates(int userId, LocalDate dateFrom, LocalDate dateTo) {
        return getAllByPredicate(userId, m -> DateTimeUtil.isBetweenFullOpen(m.getDate(), dateFrom, dateTo));
    }

    private List<Meal> getAllByPredicate(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        if (userMeals == null) return new ArrayList<>();
        return userMeals.values().stream()
                .filter(filter).sorted(COMPARATOR.reversed())
                .collect(Collectors.toList());
    }
}
