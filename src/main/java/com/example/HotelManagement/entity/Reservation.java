package com.example.HotelManagement.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
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
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    private Integer reservation_id;
    @Column(name = "guest_name")
    private String guestName;
    @Column(name = "guest_email")
    private String guestEmail;
    private String guest_phone;
    @Column(name = "check_in_date")
    private LocalDate checkInDate;
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
    @OneToMany(mappedBy = "reservation")
    private List<Payment> payments;
    @OneToMany(mappedBy = "reservation")
    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;
}