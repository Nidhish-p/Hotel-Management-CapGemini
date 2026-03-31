package com.example.HotelManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.example.HotelManagement.dto.RoomDTO;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;

@RepositoryRestResource(excerptProjection = RoomDTO.class)
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @RestResource(path = "byHotel", rel = "byHotel")
    List<Room> findByHotel_HotelId(@Param("hotelId") Integer hotelId);

    Optional<Room> findById(Integer id);
    boolean existsByRoomType(RoomType roomType);
}
