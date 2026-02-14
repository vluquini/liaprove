package com.lia.liaprove.infrastructure.validation;

import com.lia.liaprove.core.domain.user.UserRole;
import com.lia.liaprove.infrastructure.dtos.user.CreateUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RecruiterFieldsValidator implements ConstraintValidator<RecruiterFieldsRequired, CreateUserRequest> {

    @Override
    public boolean isValid(CreateUserRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let other validators handle null request
        }

        if (request.getRole() == UserRole.RECRUITER) {
            boolean valid = true;
            if (request.getCompanyName() == null || request.getCompanyName().isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Company name is required for recruiters.")
                       .addPropertyNode("companyName")
                       .addConstraintViolation();
                valid = false;
            }
            if (request.getCompanyEmail() == null || request.getCompanyEmail().isBlank()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Company email is required for recruiters.")
                       .addPropertyNode("companyEmail")
                       .addConstraintViolation();
                valid = false;
            }
            return valid;
        }

        // For non-recruiter roles, this validation does not apply
        return true;
    }
}
