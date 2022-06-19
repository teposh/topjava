package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({"classpath:spring/spring-app.xml", "classpath:spring/spring-db.xml"})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {
    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() {
        final Meal meal = mealService.get(MEAL_ID, USER_ID);
        assertMatch(meal, userMeal);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getWrong() {
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = mealService.getBetweenInclusive(
                LocalDate.of(2020, 1, 31),
                LocalDate.of(2020, 1, 31), USER_ID);
        assertMatch(meals, userMeals[6], userMeals[5], userMeals[4], userMeals[3]);
    }

    @Test
    public void getBetweenInclusiveWithEmptyDates() {
        List<Meal> meals = mealService.getBetweenInclusive(null, null, USER_ID);
        assertMatch(meals, userMeals[6], userMeals[5], userMeals[4], userMeals[3],
                userMeals[2], userMeals[1], userMeals[0]);
    }

    @Test
    public void getAll() {
        List<Meal> meals = mealService.getAll(USER_ID);
        assertMatch(meals, userMeals[6], userMeals[5], userMeals[4], userMeals[3],
                userMeals[2], userMeals[1], userMeals[0]);
    }

    @Test
    public void delete() {
        mealService.delete(MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL_ID, USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void deletedWrong() {
        assertThrows(NotFoundException.class, () -> mealService.get(adminMeals[0].getId(), USER_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        mealService.update(updated, USER_ID);
        assertMatch(mealService.get(updated.getId(), USER_ID), getUpdated());
    }

    @Test
    public void updateNotFound() {
        Meal updated = getUpdated();
        updated.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> mealService.update(updated, USER_ID));
    }

    @Test
    public void updateWrong() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = mealService.create(getNew(), USER_ID);
        Meal newMeal = getNew();
        newMeal.setId(created.getId());
        assertMatch(created, newMeal);
        assertMatch(mealService.get(created.getId(), USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        mealService.create(getNew(), USER_ID);
        assertThrows(DuplicateKeyException.class, () -> mealService.create(getNew(), USER_ID));
    }
}
