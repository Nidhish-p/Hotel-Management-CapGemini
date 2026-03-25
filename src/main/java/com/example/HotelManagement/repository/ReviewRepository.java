package com.example.HotelManagement.repository;

import com.example.HotelManagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="review")
public interface ReviewRepository extends JpaRepository<Review,Integer> {
}
