package org.zerobase.jwitter.api.aop.validation.impl;

import org.zerobase.jwitter.api.aop.validation.Password;
import org.zerobase.jwitter.api.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, UserDto.SIn> {
    @Override
    public boolean isValid(UserDto.SIn value,
                           ConstraintValidatorContext context) {
        return value.getPassword().equals(value.getPasswordConfirm());
    }

}
