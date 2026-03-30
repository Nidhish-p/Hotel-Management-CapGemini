package com.example.HotelManagement.dto;


import com.example.HotelManagement.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Date;
import java.time.LocalDate;
@Projection(name = "paymentDetails", types = Payment.class)
public interface PaymentDetailsDTO {

    @Value("#{target.payment_id}")
    Integer getPaymentId();

    @Value("#{target.amount}")
    Double getAmount();

    @Value("#{target.payment_status}")
    String getPaymentStatus();

    @Value("#{target.payment_date}")
    LocalDate getPaymentDate();

    // Keep these as they are
    @Value("#{target.reservation.guestName}")
    String getGuestName();

    @Value("#{target.reservation.guestEmail}")
    String getGuestEmail();

    @Value("#{target.reservation.guest_phone}")
    String getGuestPhone();

    @Value("#{target.reservation.checkInDate}")
    LocalDate getCheckInDate();

    @Value("#{target.reservation.checkOutDate}")
    LocalDate getCheckOutDate();

    @Value("#{target.reservation.room.roomNumber}")
    Integer getRoomNumber();

    @Value("#{target.reservation.room.hotel.name}")
    String getHotelName();

    @Value("#{target.reservation.room.hotel.location}")
    String getHotelLocation();
}