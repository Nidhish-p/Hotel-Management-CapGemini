package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.RoomDTO;
import com.example.HotelManagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(excerptProjection = RoomDTO.class)
public interface RoomRepository extends JpaRepository<Room, Integer> {
}
