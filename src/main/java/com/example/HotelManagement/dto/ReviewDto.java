package com.example.HotelManagement.dto;

import com.example.HotelManagement.entity.Review;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "reviewDto", types = Review.class)
public interface ReviewDto {

    Integer getReview_id();
    String getComment();
    int getRating();
    LocalDate getReview_date();
}
