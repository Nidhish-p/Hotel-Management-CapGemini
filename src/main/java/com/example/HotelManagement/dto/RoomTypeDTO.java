package com.example.HotelManagement.dto;

import java.math.BigDecimal;

public class RoomTypeDTO {

    private String typeName;
    private String description;
    private int maxOccupancy;
    private BigDecimal pricePerNight;

    public RoomTypeDTO(String typeName, String description, int maxOccupancy, BigDecimal pricePerNight) {
        this.typeName = typeName;
        this.description = description;
        this.maxOccupancy = maxOccupancy;
        this.pricePerNight = pricePerNight;
    }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    public BigDecimal getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(BigDecimal pricePerNight) { this.pricePerNight = pricePerNight; }
}