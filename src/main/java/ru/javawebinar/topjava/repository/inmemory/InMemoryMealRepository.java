package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Comparator<Meal> comparator = Comparator.comparing(Meal::getDateTime);

    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(m -> this.save(m, 1));
        MealsUtil.meals2.forEach(m -> this.save(m, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        repository.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            getUserMeals(userId).put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return getUserMeals(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        return getUserMeals(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return getUserMeals(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getUserMeals(userId).values().stream().sorted(comparator.reversed()).collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllFilteredByDates(int userId, LocalDate dateFrom, LocalDate dateTo) {
        return getUserMeals(userId).values().stream()
                .filter(m -> (dateFrom == null || m.getDate().compareTo(dateFrom) >= 0) && (dateTo == null || m.getDate().compareTo(dateTo) <= 0))
                .sorted(comparator.reversed())
                .collect(Collectors.toList());
    }

    private Map<Integer, Meal> getUserMeals(int userId) {
        return repository.getOrDefault(userId, new HashMap<>());
    }
}
