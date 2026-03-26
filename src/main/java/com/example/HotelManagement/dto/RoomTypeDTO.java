package com.example.HotelManagement.dto;

import java.math.BigDecimal;

import org.springframework.data.rest.core.config.Projection;

import com.example.HotelManagement.entity.RoomType;

@Projection(name = "roomTypeSummary", types = RoomType.class)
public interface RoomTypeDTO {

    String getTypeName();

    String getDescription();

    Integer getMaxOccupancy();

    BigDecimal getPricePerNight();
}