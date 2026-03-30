package com.example.HotelManagement.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReservationDetailsDTO {

    public Integer reservationId;
    public String guestName;
    public String guestEmail;

    public LocalDate checkInDate;
    public LocalDate checkOutDate;

    public Integer roomNumber;

    public String hotelName;
    public String hotelLocation;

    public Double paymentAmount;
    public String paymentStatus;
}
