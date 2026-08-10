package com.atlas.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atlas.auth.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse (HttpStatus.CONFLICT.value(),ex.getMessage()));
    }
}
