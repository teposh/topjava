package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.impl.InMemoryMealDao;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    final static int CALORIES_PER_DAY = 2000; // 1.2.1 ... Норму калорий (caloriesPerDay) сделать в коде константой.

    final static String INSERT_OR_EDIT_VIEW = "meal.jsp";

    final static String INDEX_VIEW = "meals.jsp";

    private final MealDao mealDao;

    public MealServlet() {
        mealDao = new InMemoryMealDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String action = req.getParameter("action") != null ? req.getParameter("action") : "";
        final int id = req.getParameter("id") != null ? Integer.parseInt(req.getParameter("id")) : -1;

        req.setAttribute("formatter", dateTimeFormatter);

        if (action.equals("create") || action.equals("update")) {
            req.setAttribute("meal", id >= 0 ? mealDao.get(id) : null);

            req.getRequestDispatcher(INSERT_OR_EDIT_VIEW).forward(req, resp);
        } else {
            if (action.equals("delete") && id >= 0) {
                mealDao.delete(id);
            }

            req.setAttribute("meals", MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));

            req.getRequestDispatcher(INDEX_VIEW).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Integer id = req.getParameter("id") != null ? Integer.parseInt(req.getParameter("id")) : null;
        final LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"), dateTimeFormatter);
        final int calories = Integer.parseInt(req.getParameter("calories"));
        final String description = req.getParameter("description");

        if (id != null && id >= 0) {
            mealDao.update(new Meal(id, dateTime, description, calories));
        } else {
            mealDao.add(new Meal(dateTime, description, calories));
        }

        resp.sendRedirect("meals");
    }
}
