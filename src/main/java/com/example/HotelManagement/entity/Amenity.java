package com.example.HotelManagement.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="amenityId")
    int amenityId;
    String name;
    String description;

    @ManyToMany(mappedBy = "amenities")
    private List<Hotel> hotels;

    @ManyToMany(mappedBy = "amenities",fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
    
}
