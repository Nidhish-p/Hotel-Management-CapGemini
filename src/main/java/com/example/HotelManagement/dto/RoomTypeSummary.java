package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.RoomType;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "roomTypeSummary", types = RoomType.class)
public interface RoomTypeSummary {

    String getTypeName();

    String getDescription();

    int getMaxOccupancy();

    BigDecimal getPricePerNight();
}