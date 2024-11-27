package com.example.demo.utils.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Role {
    String name();

    String message() default "{name} invalid";

    Class<? extends Enum<?>> enumRole();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
