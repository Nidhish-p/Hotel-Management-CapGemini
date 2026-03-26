package com.example.HotelManagement.repository_test;

import java.util.List;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.ReservationRepository;

@SpringBootTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void getAllReservationsTestRepo() {
        List<Reservation> reservations = reservationRepository.findAll();

        if (reservations.isEmpty()) {
            Reservation reservation = new Reservation();
            reservation.setReservation_id(1);
            reservation.setGuest_name("Test Guest");
            reservation.setGuest_email("test@example.com");
            reservation.setGuest_phone("1234567890");
            reservation.setCheck_in_date(LocalDate.of(2026, 4, 1));
            reservation.setCheck_out_date(LocalDate.of(2026, 4, 5));
            reservationRepository.save(reservation);
            reservations = reservationRepository.findAll();
        }

        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty();
    }
}
