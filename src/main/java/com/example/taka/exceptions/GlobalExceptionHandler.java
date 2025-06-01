package com.example.taka.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import java.util.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //DTO validation errors (400 bad request)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        //collect each field error into a map; {fieldName: errorMessage}
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((FieldError fieldError) -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()));

        ValidationErrorResponse payload = new ValidationErrorResponse("validation failed", errors);
        return new ResponseEntity<>(payload, HttpStatus.BAD_REQUEST);
    }

    //handle "entity not found" or other runtime excptns (404 or 400)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GenericErrorResponse> handleRuntimeException(RuntimeException ex){
        //assuming any runtimeException is a 404
        GenericErrorResponse payload = new GenericErrorResponse(ex.getMessage());
        return new ResponseEntity<>(payload, HttpStatus.NOT_FOUND);
    }

    //DTOs for the error response
    public static record ValidationErrorResponse(
            String error, //eg Validation failed
            Map<String, String> details //eg field -> message
            ){}

    public static record GenericErrorResponse(
            String error //eg "Request not found: 42
    ){}
}
