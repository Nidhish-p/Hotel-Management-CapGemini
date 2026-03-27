package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Amenity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer amenity_id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "amenities")
    private List<Hotel> hotels;
    
}
