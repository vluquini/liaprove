package com.lia.liaprove.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RecruiterFieldsValidator.class)
public @interface RecruiterFieldsRequired {
    String message() default "For Recruiter role, company name and company email are required.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
