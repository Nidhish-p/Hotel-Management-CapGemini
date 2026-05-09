package com.example.HotelManagement.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReservationResponseDTO {

    private int id;

    private String guestName;
    private String guestEmail;
    private String guestPhone;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

}