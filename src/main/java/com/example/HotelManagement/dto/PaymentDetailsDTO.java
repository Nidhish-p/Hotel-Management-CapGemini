package com.example.HotelManagement.dto;


import com.example.HotelManagement.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "paymentDTO", types = Payment.class)
public interface PaymentDetailsDTO {
    Integer getPaymentId();
    Double getAmount();
    String getPaymentStatus();
    String getPaymentDate();

    // Reservation details
    @Value("#{target.reservation.guestName}")
    String getGuestName();

    @Value("#{target.reservation.guestEmail}")
    String getGuestEmail();

    @Value("#{target.reservation.guestPhone}")
    String getGuestPhone();

    @Value("#{target.reservation.checkInDate}")
    String getCheckInDate();

    @Value("#{target.reservation.checkOutDate}")
    String getCheckOutDate();

    // Room details
    @Value("#{target.reservation.room.roomNumber}")
    Integer getRoomNumber();

    // Hotel details
    @Value("#{target.reservation.room.hotel.name}")
    String getHotelName();

    @Value("#{target.reservation.room.hotel.location}")
    String getHotelLocation();
}