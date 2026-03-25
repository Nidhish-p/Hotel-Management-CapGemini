package com.example.HotelManagement.dto;

import org.springframework.data.rest.core.config.Projection;

import com.example.HotelManagement.entity.Hotel;

@Projection(name = "hotelDTO", types = Hotel.class)
public interface HotelDTO {

    String getName();

    String getLocation();

    String getDescription();
}