package com.example.HotelManagement.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="amenityId")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer amenityId;
    String name;
    String description;

    @ManyToMany(mappedBy = "amenities")
    @JsonIgnore
    private List<Hotel> hotels;

    @ManyToMany(mappedBy = "amenities",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Room> rooms = new ArrayList<>();
    
}
