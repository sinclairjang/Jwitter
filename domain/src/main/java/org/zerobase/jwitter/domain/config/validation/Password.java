package org.zerobase.jwitter.domain.config.validation;

import org.zerobase.jwitter.domain.config.validation.impl.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "Given passwords doens't match.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
