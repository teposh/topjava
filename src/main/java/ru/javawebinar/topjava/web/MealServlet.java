package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController mealRestController;

    @Override
    public void init() {
        ClassPathXmlApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        mealRestController = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
        mealRestController.create(meal);
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                mealRestController.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        mealRestController.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                processDateTimeFilter(request);
                request.setAttribute("meals", mealRestController.getAll(
                        (LocalDate) request.getAttribute("dateFrom"), (LocalDate) request.getAttribute("dateTo"),
                        (LocalTime) request.getAttribute("timeFrom"), (LocalTime) request.getAttribute("timeTo")
                ));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private void processDateTimeFilter(HttpServletRequest request) {
        final String dateFrom = request.getParameter("dateFrom") != null ? request.getParameter("dateFrom") : "";
        final String timeFrom = request.getParameter("timeFrom") != null ? request.getParameter("timeFrom") : "";

        final String dateTo = request.getParameter("dateTo") != null ? request.getParameter("dateTo") : "";
        final String timeTo = request.getParameter("timeTo") != null ? request.getParameter("timeTo") : "";

        request.setAttribute("dateFrom", dateFrom.equals("") ? null : LocalDate.parse(dateFrom));
        request.setAttribute("timeFrom", timeFrom.equals("") ? LocalTime.MIN : LocalTime.parse(timeFrom));

        request.setAttribute("dateTo", dateTo.equals("") ? null : LocalDate.parse(dateTo));
        request.setAttribute("timeTo", timeTo.equals("") ? LocalTime.MAX : LocalTime.parse(timeTo));

        log.info("{}, {}, {}, {}", request.getAttribute("dateFrom"), request.getAttribute("dateTo"),
                request.getAttribute("timeFrom"), request.getAttribute("timeTo"));
    }
}
