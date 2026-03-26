package com.example.HotelManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Hotel {

    @Id
    int hotel_id;

    String name;
    String location;
    String description;
    
}
