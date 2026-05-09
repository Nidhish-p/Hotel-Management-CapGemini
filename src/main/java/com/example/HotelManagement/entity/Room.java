package com.example.HotelManagement.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_number")
    private Integer roomNumber;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @OneToMany(mappedBy="room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Reservation> reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id",
            foreignKey = @ForeignKey(name = "fk_room_roomtype"))
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id",
            foreignKey = @ForeignKey(name = "fk_room_hotel"))
    private Hotel hotel;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "roomamenity",
            joinColumns = @JoinColumn(name = "room_id",
                    foreignKey = @ForeignKey(name = "fk_roomamenity_room")),
            inverseJoinColumns = @JoinColumn(name = "amenityId",
                    foreignKey = @ForeignKey(name = "fk_roomamenity_amenity"))
    )
    private List<Amenity> amenities;
}
