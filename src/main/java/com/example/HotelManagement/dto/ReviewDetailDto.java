package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Review;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
@Projection(name = "reviewDetails", types = { Review.class })
public interface ReviewDetailDto {
/*
        String getComment();
        int getRating();
        LocalDate getReview_date();
        String getReservation_Guest_name();
        LocalDate getReservation_Check_in_date();
        LocalDate getReservation_Check_out_date();
        Integer getReservation_Reservation_id();
*/
    // Review
    String getComment();
    int getRating();
    LocalDate getReview_date();

    // Reservation (✅ FIXED)
    String getReservation_GuestName();
    LocalDate getReservation_CheckInDate();
    LocalDate getReservation_CheckOutDate();
    Integer getReservation_Reservation_id();
    }





