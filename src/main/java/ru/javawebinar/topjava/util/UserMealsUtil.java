package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final Map<LocalDate, Integer> sumOfCaloriesPerDay = new HashMap<>();
        for (UserMeal meal : meals) {
            final LocalDate mealDate = meal.getDateTime().toLocalDate();
            sumOfCaloriesPerDay.put(mealDate, sumOfCaloriesPerDay.getOrDefault(mealDate, 0) + meal.getCalories());
        }

        final List<UserMealWithExcess> mealsWithExcesses = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                mealsWithExcesses.add(
                        UserMealsUtil.convertToMealWithExcess(meal, sumOfCaloriesPerDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)
                );
            }
        }

        return mealsWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final Map<LocalDate, Integer> sumOfCaloriesPerDay = meals.stream()
                .collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(um -> TimeUtil.isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> UserMealsUtil.convertToMealWithExcess(um, sumOfCaloriesPerDay.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCustomCollector(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class ArrayListWithSum<T> extends ArrayList<T> {
            private int calories = 0;

            public int getCalories() {
                return calories;
            }

            public void addCalories(int calories) {
                this.calories += calories;
            }
        }

        class CustomCollector implements Collector<UserMeal, ArrayListWithSum<UserMeal>, List<UserMealWithExcess>> {
            @Override
            public Supplier<ArrayListWithSum<UserMeal>> supplier() {
                return ArrayListWithSum::new;
            }

            @Override
            public BiConsumer<ArrayListWithSum<UserMeal>, UserMeal> accumulator() {
                return (list, meal) -> {
                    list.addCalories(meal.getCalories());
                    if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        list.add(meal);
                    }
                };
            }

            @Override
            public BinaryOperator<ArrayListWithSum<UserMeal>> combiner() {
                return (list1, list2) -> {
                    list1.addCalories(list2.getCalories());
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<ArrayListWithSum<UserMeal>, List<UserMealWithExcess>> finisher() {
                return list -> list.stream()
                        .map(um -> UserMealsUtil.convertToMealWithExcess(um, list.getCalories() > caloriesPerDay))
                        .collect(Collectors.toList());
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        }

        return meals.stream()
                .collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(), new CustomCollector()))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess convertToMealWithExcess(UserMeal meal, boolean isExcess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), isExcess);
    }
}
