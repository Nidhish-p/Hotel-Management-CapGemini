package com.example.HotelManagement.repository_test;

import java.util.List;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EntityManager entityManager;

    private static final AtomicInteger RESERVATION_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 1_000_000);

    @Test
    @Transactional
    void getAllReservationsTestRepo() {
        List<Reservation> reservations = reservationRepository.findAll();

        if (reservations.isEmpty()) {
            int id = RESERVATION_SEQ.getAndIncrement();
            entityManager.createNativeQuery(
                            "insert into reservation (reservation_id, check_in_date, check_out_date, guest_email, guest_name, guest_phone, room_id) " +
                                    "values (?,?,?,?,?,?,?)")
                    .setParameter(1, id)
                    .setParameter(2, LocalDate.of(2026, 4, 1))
                    .setParameter(3, LocalDate.of(2026, 4, 5))
                    .setParameter(4, "test@example.com")
                    .setParameter(5, "Test Guest")
                    .setParameter(6, "1234567890")
                    .setParameter(7, null)
                    .executeUpdate();
            reservations = reservationRepository.findAll();
        }

        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty();
    }
}
