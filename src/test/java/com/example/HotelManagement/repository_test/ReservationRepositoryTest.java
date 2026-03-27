package com.example.HotelManagement.repository_test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.RoomRepo;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RoomRepo roomRepository;

    //2nd page

    @Test
    void getAllReservationsTestRepo() {
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty();
    }

    @Test
    void getReservationByGuestEmail() {
        String email = "tanvi@test.com";

        List<Reservation> list = reservationRepository.findByGuestEmail(email);

        assertThat(list).isNotNull();
        assertThat(list).allMatch(r -> r.getGuestEmail().equals(email));
    }

    @Test
    void getReservationByCheckInDate() {
        LocalDate date = LocalDate.of(2024, 11, 1); // match your data.sql
        List<Reservation> list = reservationRepository.findByCheckInDate(date);
        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }
    
    @Test
    void getReservationByGuestName() {
        String name = "Tanvi"; 

        List<Reservation> list = reservationRepository.findByGuestName(name);

        assertThat(list).isNotNull();
        assertThat(list).allMatch(r -> r.getGuestName().equals(name));
    }
    
    @Test
    void getReservationBetweenDates() {
        LocalDate start = LocalDate.of(2024, 10, 1);
        LocalDate end = LocalDate.of(2024, 10, 5);

        List<Reservation> list = reservationRepository.findByCheckInDateBetween(start, end);

        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThan(0);
    }
    
    @Test
    void getReservationByCheckOutDate() {
        LocalDate date = LocalDate.of(2025, 3, 27);

        List<Reservation> list = reservationRepository.findByCheckOutDate(date);

        assertThat(list).isNotNull();
    }

    @Test
    void addReservation() {
        Room room = roomRepository.findById(1).orElse(null);
        assertThat(room).isNotNull();

        Reservation r = new Reservation();
        r.setGuestName("Jason Derulo");
        r.setGuestEmail("jason@test.com");
        r.setGuest_phone("1234567890");
        r.setCheckInDate(LocalDate.of(2025, 10, 1));
        r.setCheckOutDate(LocalDate.of(2025, 10, 5));
        r.setRoom(room);
        room.setIsAvailable(false);
        roomRepository.save(room);

        Reservation saved = reservationRepository.save(r);

        assertThat(saved).isNotNull();
        assertThat(saved.getReservation_id()).isNotNull();
    }
    

    // 3rd page 

        @Test
    void getRoomByReservation() {
        Reservation r = reservationRepository.findById(1).orElse(null);
        Room room = r.getRoom();
        assertThat(room).isNotNull();
    }

    @Test
    void getReviewFromReservation() {
        Reservation r = reservationRepository.findById(1).orElse(null);
        List<Review> list = r.getReviews();
        assertThat(list).isNotNull();
    }
    
    @Test
    void getPaymentFromReservation() {
        Reservation r = reservationRepository.findById(1).orElse(null);
        List<Payment> list = r.getPayments();
        assertThat(list).isNotNull();
    }

    @Test
    void getHotelFromReservation() {
        Reservation r = reservationRepository.findById(1).orElse(null);
        Hotel hotel = r.getRoom().getHotel();
        assertThat(hotel).isNotNull();
    }
    
}
