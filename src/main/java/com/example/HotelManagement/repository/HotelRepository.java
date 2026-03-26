package com.example.HotelManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.example.HotelManagement.dto.HotelDTO;
import com.example.HotelManagement.entity.Hotel;

@Repository
@RepositoryRestResource(excerptProjection = HotelDTO.class)
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    
}
