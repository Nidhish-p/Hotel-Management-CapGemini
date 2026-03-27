package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Amenity {

    @Id
    int amenity_id;
    String name;
    String description;

    @ManyToMany(mappedBy = "amenities")
    private List<Hotel> hotels;
    
}
