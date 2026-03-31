package com.example.HotelManagement.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import com.example.HotelManagement.dto.RoomTypeDTO;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;

@Repository
@RepositoryRestResource(path = "roomtypes", excerptProjection = RoomTypeDTO.class)
@Validated
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    List<RoomType> findByTypeNameStartingWith(String typeName);
    List<RoomType> findByTypeNameContainingIgnoreCaseOrderByRoomTypeIdDesc(String typeName);
    List<RoomType> findByDescriptionStartingWith(String description);
    List<RoomType> findByPricePerNight(BigDecimal pricePerNight);
    List<RoomType> findByMaxOccupancy(Integer maxOccupancy);
    List<Room> findByRoomTypeId(Long id);
    boolean existsByTypeName(String typeName);
}
