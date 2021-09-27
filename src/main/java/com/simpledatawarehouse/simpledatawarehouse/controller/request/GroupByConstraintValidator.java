package com.simpledatawarehouse.simpledatawarehouse.controller.request;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

import static com.simpledatawarehouse.simpledatawarehouse.util.StringUtils.cleanse;
import static java.util.Arrays.stream;

public class GroupByConstraintValidator implements ConstraintValidator<GroupByConstraint, String> {

    public static final List<String> validGroupByValues =
            stream(GroupByValues.values()).map(value -> value.name().toLowerCase()).collect(Collectors.toList());

    @Override
    public void initialize(GroupByConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
          if (value.contains(",")) {
              return stream(cleanse(value).split(",")).allMatch(validGroupByValues::contains);
          } else {
            return isValid(value);
          }
        }

        return true;
    }

    boolean isValid(String group) {
        return validGroupByValues
                .stream().anyMatch(validGroupByValue -> validGroupByValue.contains(cleanse(group)));
    }

}
