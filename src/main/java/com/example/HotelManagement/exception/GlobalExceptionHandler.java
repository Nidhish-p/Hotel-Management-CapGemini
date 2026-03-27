package com.example.HotelManagement.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RoomNotAvailableException.class)
    public ResponseEntity<String> handleRoomNotAvailable(RoomNotAvailableException ex) {
        return ResponseEntity
                .status(400)
                .body(ex.getMessage());
    }
}
