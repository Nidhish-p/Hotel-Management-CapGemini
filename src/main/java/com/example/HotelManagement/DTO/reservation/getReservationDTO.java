package com.example.HotelManagement.DTO.reservation;

import java.time.LocalDate;
import org.springframework.data.rest.core.config.Projection;
import com.example.HotelManagement.entity.Reservation;

@Projection(name = "getReservationDTO", types = Reservation.class)
public interface getReservationDTO {

    Integer getReservation_id();
    String getGuest_name();
    String getGuest_email();
    String getGuest_phone();
    LocalDate getCheck_in_date();
    LocalDate getCheck_out_date();
}