package com.lia.liaprove.infrastructure.validation;

import com.lia.liaprove.infrastructure.dtos.question.AlternativeRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ExactlyOneCorrectAlternativeValidator implements ConstraintValidator<ExactlyOneCorrectAlternative, List<AlternativeRequestDto>> {

    @Override
    public void initialize(ExactlyOneCorrectAlternative constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<AlternativeRequestDto> alternatives, ConstraintValidatorContext context) {
        if (alternatives == null || alternatives.isEmpty()) {
            // This is not the responsibility of this validator.
            // @NotNull and @Size should be used for this.
            return true;
        }

        long correctCount = alternatives.stream()
                .filter(AlternativeRequestDto::correct)
                .count();

        if (correctCount != 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Deve existir exatamente 1 alternativa correta, encontradas: " + correctCount)
                   .addPropertyNode("alternatives") // Associar a violação ao nó da propriedade
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}
