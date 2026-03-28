package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Entity
@Data
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int amenity_id;
    String name;
    String description;

    @ManyToMany(mappedBy = "amenities")
    private List<Hotel> hotels;

    @ManyToMany(mappedBy = "amenities")
    private List<Room> rooms;
    
}
