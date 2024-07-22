package org.zerobase.jwitter.domain.config.validation.impl;

import org.zerobase.jwitter.domain.config.validation.Password;
import org.zerobase.jwitter.domain.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, User> {
    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        return value.getPassword().equals(value.getPasswordConfirm());
    }
}
