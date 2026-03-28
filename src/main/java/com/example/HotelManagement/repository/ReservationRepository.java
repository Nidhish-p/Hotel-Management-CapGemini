package com.example.HotelManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HotelManagement.dto.ReservationDTO;
import com.example.HotelManagement.entity.Reservation;

@RepositoryRestResource(
    path = "reservations",
    excerptProjection = ReservationDTO.class 
)
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByCheckInDate(LocalDate date);

    List<Reservation> findByCheckOutDate(LocalDate date);

    List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end);

    List<Reservation> findByGuestName(String name);

    List<Reservation> findByGuestEmail(String email);
    
}
