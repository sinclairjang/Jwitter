package org.zerobase.jwitter.domain.config.validation;

import org.zerobase.jwitter.domain.config.validation.impl.FollowValidator;
import org.zerobase.jwitter.domain.config.validation.impl.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = FollowValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface Follow {
    String message() default "You can't follow yourself.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
