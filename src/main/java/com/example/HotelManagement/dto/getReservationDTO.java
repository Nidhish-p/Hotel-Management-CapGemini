package com.example.HotelManagement.dto;

import java.time.LocalDate;

import org.springframework.data.rest.core.config.Projection;

import com.example.HotelManagement.entity.Reservation;

@Projection(name = "getReservationDTO", types = Reservation.class)
public interface getReservationDTO {

    Integer getReservation_id();
    String getGuestName();
    String getGuestEmail();
    String getGuest_phone();
    LocalDate getCheckInDate();
    LocalDate getCheckOutDate();
}