package com.example.HotelManagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Amenity {

    @Id
    int amenity_id;
    String name;
    String description;
    
}
