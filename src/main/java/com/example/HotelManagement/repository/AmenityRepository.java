package com.example.HotelManagement.repository;

import com.example.HotelManagement.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    List<Amenity> findByName(String name);
}
