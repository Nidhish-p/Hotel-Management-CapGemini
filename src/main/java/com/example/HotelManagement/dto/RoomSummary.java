package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Room;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "roomSummary" ,types = Room.class)
public interface RoomSummary {
    Integer getRoomNumber();
    Integer getRoomTypeId();
    Boolean getIsAvailable();
}
