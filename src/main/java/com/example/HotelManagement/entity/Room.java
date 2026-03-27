package com.example.HotelManagement.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    Integer roomId;
    Integer roomNumber;
    Integer roomTypeId;
    Boolean isAvailable;

    @OneToMany(mappedBy="room")
    private List<Reservation> reservation;

    @ManyToOne
    @JoinColumn(name="hotel_id")
    private Hotel hotel;

}
