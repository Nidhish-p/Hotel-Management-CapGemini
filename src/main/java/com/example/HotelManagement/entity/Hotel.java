package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hotel_id")
    int hotelId;

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
    @OneToMany(mappedBy = "hotel")
    private List<Room> rooms;
    
}
