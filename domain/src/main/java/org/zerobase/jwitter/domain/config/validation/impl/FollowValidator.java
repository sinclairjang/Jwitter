package org.zerobase.jwitter.domain.config.validation.impl;

import org.zerobase.jwitter.domain.config.validation.Follow;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class FollowValidator implements ConstraintValidator<Follow, Object[]> {
    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value[0] == null || value[1] == null) {
            return true;
        }

        if (!(value[0] instanceof Long)
                || !(value[1] instanceof Long)) {
            throw new IllegalArgumentException(
                    "Illegal method signature, expected two parameters of type LocalDate.");
        }

        return !value[0].equals(value[1]);
    }
}
