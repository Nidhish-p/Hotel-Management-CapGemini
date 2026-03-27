package com.example.HotelManagement.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<String> handleRoomNotAvailable(RoomNotAvailableException ex) {
        return ResponseEntity
                .status(400)
                .body(ex.getMessage());}
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("Validation error");

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    }

