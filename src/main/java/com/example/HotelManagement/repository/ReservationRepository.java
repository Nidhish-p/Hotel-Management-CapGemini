package com.example.HotelManagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.example.HotelManagement.dto.ReservationDTO;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Room;

@RepositoryRestResource(
    path = "reservations",
    excerptProjection = ReservationDTO.class
)
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByCheckInDate(@Param("date") LocalDate date);

    List<Reservation> findByCheckOutDate(@Param("date") LocalDate date);

    List<Reservation> findByCheckInDateBetween(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    List<Reservation> findByCheckOutDateBetween(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    List<Reservation> findByGuestNameContainingIgnoreCase(
        @Param("name") String guestName
    );
    List<Reservation> findByGuestEmailContainingIgnoreCase(
        @Param("email") String guestEmail
    );

    List<Reservation> findByRoomAndCheckOutDateAfterAndCheckInDateBefore(Room room,LocalDate checkIn,LocalDate checkOut);
}