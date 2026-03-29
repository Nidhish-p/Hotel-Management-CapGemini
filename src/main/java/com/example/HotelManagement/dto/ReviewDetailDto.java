package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Review;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
@Projection(name = "reviewDetails", types = Review.class)
public interface ReviewDetailDto {
    // Reservation fields
    /*
    String getReservation_Guest_name();
    LocalDate getReservation_Check_in_date();
    LocalDate getReservation_Check_out_date();
    Integer getReservation_Reservation_id();

    // Room fields
    Integer getReservation_Room_RoomNumber();

    // ✅ RoomType name
    String getReservation_Room_RoomType_Type_name();

     */
    // 🔹 Reservation fields
    String getReservation_GuestName();
    LocalDate getReservation_CheckInDate();
    LocalDate getReservation_CheckOutDate();
    Integer getReservation_Reservation_id();

    // 🔹 Room fields
    Integer getReservation_Room_RoomNumber();

    // 🔹 RoomType field
    String getReservation_Room_RoomType_TypeName();
}

