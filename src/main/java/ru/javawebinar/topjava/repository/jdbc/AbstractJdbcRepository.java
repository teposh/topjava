package ru.javawebinar.topjava.repository.jdbc;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class AbstractJdbcRepository<T> {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    protected void validate(T obj) {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
