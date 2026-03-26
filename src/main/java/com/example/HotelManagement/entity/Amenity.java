package com.example.HotelManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Amenity {

    @Id
    int amenity_id;
    String name;
    String description;
    
}
