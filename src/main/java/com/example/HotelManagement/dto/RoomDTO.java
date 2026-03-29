package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(name = "roomSummary" ,types = Room.class)
public interface RoomDTO {
    Integer getRoomId();
    Integer getRoomNumber();
    Boolean getIsAvailable();
    RoomType getRoomType();
    List<Amenity> getAmenities();
}
