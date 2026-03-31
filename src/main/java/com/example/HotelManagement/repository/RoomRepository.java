package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.RoomSummary;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(excerptProjection = RoomSummary.class)
public interface RoomRepository extends JpaRepository<Room, Integer> {
    boolean existsByRoomType(RoomType roomType);
}
