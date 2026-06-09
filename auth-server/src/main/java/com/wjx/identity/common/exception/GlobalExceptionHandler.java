package com.wjx.identity.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handlerBusiness(
            BusinessException e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
