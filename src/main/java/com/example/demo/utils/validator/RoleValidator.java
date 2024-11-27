package com.example.demo.utils.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<Role, String> {
    private List<? extends Enum<?>> ListRole = new ArrayList<>();

    @Override
    public void initialize(Role contactNumber) {
        this.ListRole = Stream.of(contactNumber.enumRole().getEnumConstants()).toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext cxt) {
        return this.ListRole.contains(value);
    }
}
