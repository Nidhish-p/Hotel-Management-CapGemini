package com.example.HotelManagement.exception;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.repository.HotelRepository;

@Component
@RepositoryEventHandler(Hotel.class)
public class HotelDuplicateGuard {

    private final HotelRepository hotelRepository;

    public HotelDuplicateGuard(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @HandleBeforeCreate
    public void preventDuplicateOnCreate(Hotel hotel) {
        if (hotel.getName() != null && hotelRepository.existsByName(hotel.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate hotel name");
        }
    }

    @HandleBeforeSave
    public void preventDuplicateOnUpdate(Hotel hotel) {
        if (hotel.getName() == null) {
            return;
        }
        boolean duplicate = hotelRepository.existsByNameAndHotelIdNot(hotel.getName(), hotel.getHotelId());
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate hotel name");
        }
    }
}
