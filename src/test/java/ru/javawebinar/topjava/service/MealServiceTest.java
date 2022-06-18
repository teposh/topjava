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
        final Meal meal = mealService.get(USER_MEALS[0].getId(), USER_ID);
        assertMatch(meal, USER_MEALS[0]);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getWrong() {
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEALS[0].getId(), ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = mealService.getBetweenInclusive(
                LocalDate.of(2020, 1, 31),
                LocalDate.of(2020, 1, 31), USER_ID);
        assertMatch(meals, USER_MEALS[6], USER_MEALS[5], USER_MEALS[4], USER_MEALS[3]);
    }

    @Test
    public void getAll() {
        List<Meal> meals = mealService.getAll(USER_ID);
        assertMatch(meals, USER_MEALS[6], USER_MEALS[5], USER_MEALS[4], USER_MEALS[3],
                USER_MEALS[2], USER_MEALS[1], USER_MEALS[0]);
    }

    @Test
    public void delete() {
        mealService.delete(USER_MEALS[0].getId(), USER_ID);
        assertThrows(NotFoundException.class, () -> mealService.get(USER_MEALS[0].getId(), USER_ID));
    }

    @Test
    public void deletedNotFound() {
        assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void deletedWrong() {
        assertThrows(NotFoundException.class, () -> mealService.get(ADMIN_MEALS[0].getId(), USER_ID));
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        mealService.update(updated, USER_ID);
        assertMatch(updated, mealService.get(updated.getId(), USER_ID));
    }

    @Test
    public void updateWrong() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = mealService.create(getNew(), USER_ID);
        assertMatch(created, mealService.get(created.getId(), USER_ID));
    }

    @Test
    public void createSameTime() {
        mealService.create(getNew(), USER_ID);
        assertThrows(DuplicateKeyException.class, () -> mealService.create(getNew(), USER_ID));
    }
}
