package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.RoomType;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "roomTypeSummary", types = RoomType.class)
public interface RoomTypeDTO {

    Integer getRoomTypeId();

    String getTypeName();

    String getDescription();

    Integer getMaxOccupancy();

    BigDecimal getPricePerNight();
}