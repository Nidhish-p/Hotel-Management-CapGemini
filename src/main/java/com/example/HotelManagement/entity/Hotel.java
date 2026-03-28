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
    @Column(name = "hotel_id", nullable = false)
    private int hotelId;

    @Column(name = "name", length = 255, nullable = true)
    private String name;

    @Column(name = "location", length = 255, nullable = true)
    private String location;

    @Column(name = "description", length = 255, nullable = true)
    private String description;

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
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;
    
}
