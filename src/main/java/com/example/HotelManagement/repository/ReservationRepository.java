package com.example.HotelManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HotelManagement.DTO.reservation.getReservationDTO;
import com.example.HotelManagement.entity.Reservation;

@RepositoryRestResource(
    path = "reservations",
    excerptProjection = getReservationDTO.class 
)
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}