package com.example.HotelManagement.exception;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.HotelManagement.entity.Hotel;

@Component
@RepositoryEventHandler(Hotel.class)
public class HotelCreateBlocker {

    @HandleBeforeCreate
    public void blockCreate(Hotel hotel) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Hotel creation is not allowed via API");
    }
}
