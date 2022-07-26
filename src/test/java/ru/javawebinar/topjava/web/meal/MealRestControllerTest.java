package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

public class MealRestControllerTest extends AbstractControllerTest {
    @Autowired
    private MealService mealService;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(MealRestController.REST_URL + "/" + MEAL1_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void getAll() throws Exception {
        MvcResult res = perform(MockMvcRequestBuilders.get(MealRestController.REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThatResultEqualsToMealsList(res, mealTos);
    }

    @Test
    void create() throws Exception {
        Meal newMeal = getNew();
        var res = perform(MockMvcRequestBuilders.post(MealRestController.REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        Meal created = MEAL_MATCHER.readFromJson(res);
        int newId = created.id();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    void update() throws Exception {
        Meal updated = getUpdated();
        perform(MockMvcRequestBuilders.put(MealRestController.REST_URL + "/" + updated.getId())
                .content(JsonUtil.writeValue(updated))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(mealService.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(MealRestController.REST_URL + "/" + MEAL1_ID))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> mealService.get(MEAL1_ID, USER_ID));
    }

    @Test
    void getBetween() throws Exception {
        MvcResult res = perform(MockMvcRequestBuilders.get(MealRestController.REST_URL + "/filter")
                .param("startDate", "2020-01-31")
                .param("startTime", "01:00:00")
                .param("endDate", "2020-01-31")
                .param("endTime", "22:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThatResultEqualsToMealsList(res, List.of(mealTo7, mealTo6, mealTo5));
    }

    @Test
    void getBetweenWithNulls() throws Exception {
        MvcResult res = perform(MockMvcRequestBuilders.get(MealRestController.REST_URL + "/filter"
                + "?startDate=2020-01-31"
                + "&startTime="))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        assertThatResultEqualsToMealsList(res, List.of(mealTo7, mealTo6, mealTo5, mealTo4));
    }

    private static void assertThatResultEqualsToMealsList(MvcResult res, List<MealTo> mealTos)
            throws UnsupportedEncodingException {
        assertThat(JsonUtil.readValues(res.getResponse().getContentAsString(), MealTo.class))
                .isEqualTo(mealTos);
    }
}
