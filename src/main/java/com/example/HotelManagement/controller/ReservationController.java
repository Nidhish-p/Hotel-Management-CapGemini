package com.example.HotelManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.HotelManagement.dto.ReservationDetailsDTO;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.exception.ReservationConflictException;
import com.example.HotelManagement.exception.RoomNotAvailableException;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.RoomRepository;
    
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/{id}/details")
    @Transactional
    public ReservationDetailsDTO getDetails(@PathVariable Integer id) {

        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        ReservationDetailsDTO dto = new ReservationDetailsDTO();

        dto.reservationId = r.getReservation_id();
        dto.guestName = r.getGuestName();
        dto.guestEmail = r.getGuestEmail();
        dto.checkInDate = r.getCheckInDate();
        dto.checkOutDate = r.getCheckOutDate();

        Room room = r.getRoom();
        if (room != null) {
            dto.roomNumber = room.getRoomNumber();

            if (room.getHotel() != null) {
                dto.hotelName = room.getHotel().getName();
                dto.hotelLocation = room.getHotel().getLocation();
            }
        }

        if (r.getPayments() != null && !r.getPayments().isEmpty()) {
            Payment p = r.getPayments().get(0);

            dto.paymentAmount = p.getAmount();
            dto.paymentStatus = p.getPayment_status();
        }

        return dto;
    }

    @PostMapping
    public Reservation addReservation(@RequestBody Reservation request) {
        Room room = roomRepository.findById(request.getRoom().getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getIsAvailable()) {
            throw new RoomNotAvailableException("Room is not available");
        }

        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Check-out must be after check-in"
            );
        }

        List<Reservation> conflicts = reservationRepository
                .findByRoomAndCheckOutDateAfterAndCheckInDateBefore(
                        room,
                        request.getCheckInDate(),
                        request.getCheckOutDate());

        if (!conflicts.isEmpty()) {
            throw new ReservationConflictException("Room already booked for selected dates");
        }

        request.setRoom(room);

        return reservationRepository.save(request);
    }


}



