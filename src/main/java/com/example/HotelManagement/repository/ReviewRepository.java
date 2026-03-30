package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.ReviewDto;
import com.example.HotelManagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path="review", excerptProjection = ReviewDto.class)
public interface ReviewRepository extends JpaRepository<Review,Integer> {
   // @RestResource(path = "byHotel", rel = "byHotel")
    List<Review> findDistinctByReservationRoomHotelHotelId(Integer hotelId);
}
