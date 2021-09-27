package com.simpledatawarehouse.simpledatawarehouse.controller;

import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByIsNeededException;
import com.simpledatawarehouse.simpledatawarehouse.exception.GroupingByNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
