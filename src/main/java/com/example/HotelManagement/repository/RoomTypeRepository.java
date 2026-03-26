package com.example.HotelManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.HotelManagement.entity.*;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "roomtypes")
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

}