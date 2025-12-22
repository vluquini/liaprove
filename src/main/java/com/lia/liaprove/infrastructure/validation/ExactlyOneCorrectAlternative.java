package com.lia.liaprove.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = ExactlyOneCorrectAlternativeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExactlyOneCorrectAlternative {
    String message() default "The multiple choice question must have exactly one correct alternative.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
