package ru.javawebinar.topjava.to;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Objects;

public class MealTo {
    private final Integer id;

    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    private final boolean excess;

    // https://stackoverflow.com/questions/70143308/cannot-construct-instance-of-com-domain-user-no-creators-like-default-constr
    @JsonCreator
    public MealTo(@JsonProperty("id") Integer id,
                  @JsonProperty("dateTime") LocalDateTime dateTime,
                  @JsonProperty("description") String description,
                  @JsonProperty("calories") int calories,
                  @JsonProperty("excess") boolean excess) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public MealTo(Meal that) {
        this(that, true);
    }

    public MealTo(Meal that, boolean isExcess) {
        this.id = that.getId();
        this.dateTime = that.getDateTime();
        this.description = that.getDescription();
        this.calories = that.getCalories();
        this.excess = isExcess;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public boolean isExcess() {
        return excess;
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTo mealTo = (MealTo) o;
        return calories == mealTo.calories
                && excess == mealTo.excess
                && Objects.equals(id, mealTo.id)
                && Objects.equals(dateTime, mealTo.dateTime)
                && Objects.equals(description, mealTo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTime, description, calories, excess);
    }
}
