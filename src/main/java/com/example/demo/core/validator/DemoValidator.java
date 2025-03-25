package com.example.demo.core.validator;

public interface DemoValidator<T> {
    Enum<?> validate(T entity);
}
