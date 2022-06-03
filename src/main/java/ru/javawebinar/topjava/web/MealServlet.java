package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.InMemoryMealDao;
import ru.javawebinar.topjava.dao.MealDao;
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

    static final int CALORIES_PER_DAY = 2000; // 1.2.1 ... Норму калорий (caloriesPerDay) сделать в коде константой.

    static final String INSERT_OR_EDIT_VIEW = "meal.jsp";

    static final String INDEX_VIEW = "meals.jsp";

    static final String APP_ROOT = "meals";

    private MealDao mealDao;

    @Override
    public void init() throws ServletException {
        mealDao = new InMemoryMealDao();
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String action = req.getParameter("action") != null ? req.getParameter("action") : "";
        log.info("{} {}", req.getMethod(), this.getRequestPath(req));
        switch (action) {
            case "create": {
                req.getRequestDispatcher(INSERT_OR_EDIT_VIEW).forward(req, resp);
                break;
            }
            case "update": {
                final int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("meal", mealDao.get(id));
                req.getRequestDispatcher(INSERT_OR_EDIT_VIEW).forward(req, resp);
                break;
            }
            case "delete": {
                final int id = Integer.parseInt(req.getParameter("id"));
                log.info("DELETE {}", mealDao.get(id));
                mealDao.delete(id);
                resp.sendRedirect(APP_ROOT);
                break;
            }
            default: {
                req.setAttribute("meals", MealsUtil.filteredByStreams(mealDao.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                req.setAttribute("formatter", dateTimeFormatter);
                req.getRequestDispatcher(INDEX_VIEW).forward(req, resp);
                break;
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8"); // ... Проблемы с кодировкой в POST (кракозябры). ...

        log.info("{} {}", req.getMethod(), this.getRequestPath(req));

        final Meal meal = new Meal(
                req.getParameter("id") != null ? Integer.parseInt(req.getParameter("id")) : null,
                LocalDateTime.parse(req.getParameter("dateTime")),
                req.getParameter("description"),
                Integer.parseInt(req.getParameter("calories"))
        );

        if (meal.getId() == null) {
            log.info("CREATE {}", meal);
            mealDao.add(meal);
        } else {
            log.info("UPDATE {}", meal);
            mealDao.update(meal);
        }

        resp.sendRedirect(APP_ROOT);
    }

    private String getRequestPath(HttpServletRequest req) {
        String buf = req.getRequestURI();
        if (req.getQueryString() != null) {
            buf += "?" + req.getQueryString();
        }
        return buf;
    }
}
