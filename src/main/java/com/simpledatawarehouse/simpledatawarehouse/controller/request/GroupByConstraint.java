package com.simpledatawarehouse.simpledatawarehouse.controller.request;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GroupByConstraintValidator.class)
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupByConstraint {
    String message() default "Invalid groupBy value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
