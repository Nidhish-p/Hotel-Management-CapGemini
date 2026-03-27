package com.example.HotelManagement.repository;

import com.example.HotelManagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path="review")
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findDistinctByReservationRoomHotelHotelId(Integer hotelId);
}
