package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
@RequestMapping("/meals")
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    private final MealService mealService;

    JspMealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping
    public String indexView(HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        request.setAttribute("meals",
                MealsUtil.getFilteredTos(
                        mealService.getBetweenInclusive(startDate, endDate, SecurityUtil.authUserId()),
                        SecurityUtil.authUserCaloriesPerDay(),
                        startTime,
                        endTime
                )
        );
        return "meals";
    }

    @GetMapping("/create")
    public String createView(Model model) {
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping("/update")
    public String updateView(HttpServletRequest request) {
        request.setAttribute("meal", mealService.get(getId(request), SecurityUtil.authUserId()));
        return "mealForm";
    }

    @GetMapping("/delete")
    public String deleteView(HttpServletRequest request) {
        mealService.delete(getId(request), SecurityUtil.authUserId());
        return "redirect:/meals";
    }

    @PostMapping
    public String createOrUpdate(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");

        int userId = SecurityUtil.authUserId();
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories"))
        );

        if (StringUtils.hasLength(request.getParameter("id"))) {
            assureIdConsistent(meal, getId(request));
            mealService.update(meal, userId);
        } else {
            checkNew(meal);
            mealService.create(meal, userId);
        }

        return "redirect:/meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
