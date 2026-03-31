package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.AmenityDTO;
import com.example.HotelManagement.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource(excerptProjection = AmenityDTO.class)
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    List<Amenity> findByName(String name);
}
