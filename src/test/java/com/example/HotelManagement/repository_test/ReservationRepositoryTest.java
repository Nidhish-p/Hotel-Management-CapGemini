package com.example.HotelManagement.repository_test;

import java.util.List;

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

        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty();
    }
}
