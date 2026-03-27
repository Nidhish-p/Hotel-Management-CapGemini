package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.RoomTypeDTO;
import com.example.HotelManagement.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "roomtypes", excerptProjection = RoomTypeDTO.class)
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {
}
