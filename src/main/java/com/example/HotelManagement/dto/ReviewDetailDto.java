package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Review;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
@Data
@Getter
@Setter
public class ReviewDetailDto {
    private Integer reviewId;
    private String comment;
    private int rating;
    private LocalDate reviewDate;

    private String guestName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private Integer roomNumber;
    private String roomType;

}

