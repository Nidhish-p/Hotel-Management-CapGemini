package com.example.HotelManagement.dto;


import com.example.HotelManagement.entity.Amenity;
import org.springframework.data.rest.core.config.Projection;

@Projection(name="Amenity",types = Amenity.class)
public interface AmenityDTO {

    String getName();
    String getDescription();
}
