package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.controller.request.GroupByConstraintValidator;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByIsNeededException;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Objects.requireNonNull;

@ControllerAdvice
public class ExceptionHandlingController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GroupingByNotSupportedException.class)
    public ResponseEntity<String> groupingByIsNotSupported() {
        return ResponseEntity.badRequest().body("groupBy is not supported");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GroupingByIsNeededException.class)
    public ResponseEntity<String> groupingIsNeeded() {
        return ResponseEntity.badRequest().body("groupBy is needed");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> groupingByValueIsInvalid(MethodArgumentNotValidException exception) {
        return ResponseEntity
                .badRequest()
                .body(format("'%s' is a invalid value for groupBy, valid values are: %s",
                        requireNonNull(exception.getFieldError("groupBy")).getRejectedValue(),
                        join(", ", GroupByConstraintValidator.validGroupByValues)));
    }
}
