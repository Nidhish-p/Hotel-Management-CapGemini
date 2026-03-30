package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Date;

@Projection(name = "hotelDTO", types = Payment.class)
public interface PaymentDTO {

    Double getAmount();
    Date getPayment_date();
    String getPayment_status();
    @Value("#{target.reservation.reservation_id}")
    String getReservation_id();
}
