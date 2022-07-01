package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id = ?1 AND m.user.id = ?2")
    int delete(int id, int userId);

    Meal getByIdAndUserId(int id, int userId);

    List<Meal> getAllByUserId(int userId, Sort sort);

    List<Meal> getAllByDateTimeGreaterThanEqualAndDateTimeLessThanAndUserId(LocalDateTime startDateTime,
                                                                            LocalDateTime endDateTime,
                                                                            int userId,
                                                                            Sort sort);
}
