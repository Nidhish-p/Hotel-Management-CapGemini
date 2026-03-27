package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int hotel_id;

    String name;
    String location;
    String description;

    @ManyToMany
    @JoinTable(
        name = "hotelamenity",
        joinColumns = @JoinColumn(
                name = "hotel_id",
                foreignKey = @ForeignKey(name = "fk_hotelamenity_hotel")
        ),
        inverseJoinColumns = @JoinColumn(
                name = "amenity_id",
                foreignKey = @ForeignKey(name = "fk_hotelamenity_amenity")
        )
    )
    private List<Amenity> amenities;
    
}
