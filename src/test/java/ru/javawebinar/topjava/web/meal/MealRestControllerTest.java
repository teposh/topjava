package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

public class MealRestControllerTest extends AbstractControllerTest {
    private static final String contentType = "application/json";

    @Autowired
    private MealService mealService;

    @Test
    void _get() throws Exception {
        MvcResult res = perform(get(MealRestController.REST_URL + "/" + MEAL1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(contentType))
                .andReturn();
        Meal actual = JsonUtil.readValue(res.getResponse().getContentAsString(), Meal.class);
        MEAL_MATCHER.assertMatch(actual, meal1);
    }

    @Test
    void getAll() throws Exception {
        MvcResult res = perform(get(MealRestController.REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(contentType))
                .andReturn();
        List<MealTo> actual = JsonUtil.readValues(res.getResponse().getContentAsString(), MealTo.class);
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(MealsUtil.getTos(meals, MealsUtil.DEFAULT_CALORIES_PER_DAY));
    }

    @Test
    void create() throws Exception {
        var req = post(MealRestController.REST_URL).contentType(contentType)
                .content(JsonUtil.writeValue(getNew()));
        MvcResult res = perform(req)
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(contentType))
                .andReturn();
        Meal created = JsonUtil.readValue(res.getResponse().getContentAsString(), Meal.class);
        MEAL_MATCHER.assertMatch(created, mealService.get(created.getId(), USER_ID));

    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();
        var req = put(MealRestController.REST_URL + "/" + updated.getId())
                .content(JsonUtil.writeValue(updated))
                .contentType(contentType);
        perform(req)
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(mealService.get(updated.getId(), USER_ID), getUpdated());

    }

    @Test
    void _delete() throws Exception {
        perform(delete(MealRestController.REST_URL + "/" + MEAL1_ID))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getBetween() throws Exception {
        MvcResult res = perform(get(MealRestController.REST_URL
                + "/filter?start=2020-01-30T00:00:00&end=2020-01-30T14:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(contentType))
                .andReturn();
        List<MealTo> actual = JsonUtil.readValues(res.getResponse().getContentAsString(), MealTo.class);
        assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(MealsUtil.getTos(List.of(meal2, meal1), MealsUtil.DEFAULT_CALORIES_PER_DAY));
    }
}
