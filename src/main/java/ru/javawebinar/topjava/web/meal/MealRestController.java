package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
public class MealRestController {
    private final MealService service;

    public MealRestController(MealService mealService) {
        this.service = mealService;
    }

    public Meal create(Meal meal) {
        return service.create(meal, SecurityUtil.authUserId());
    }

    public void delete(int id) {
        service.delete(id, SecurityUtil.authUserId());
    }

    /*
     * вот здесь непонятно кажется надо вернуть MealTo? (но такого метода нет в MealsUtil)
     * с другой стороны зачем нам в форме редактирования знать что за день превышение...
     * + если MealTo только для списка то название путает MealListItemTo.
     */
    public Meal get(int id) {
        return service.get(id, SecurityUtil.authUserId());
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(SecurityUtil.authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(LocalDate dateFrom, LocalDate dateTo, LocalTime timeFrom, LocalTime timeTo) {
        return MealsUtil.getFilteredTos(service.getAll(SecurityUtil.authUserId(), dateFrom, dateTo),
                SecurityUtil.authUserCaloriesPerDay(), timeFrom, timeTo);
    }

    public void update(Meal meal) {
        service.update(meal, SecurityUtil.authUserId());
    }
}
