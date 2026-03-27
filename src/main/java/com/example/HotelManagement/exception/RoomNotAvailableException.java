package com.example.HotelManagement.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super("Room is not available for booking");
    }
}
