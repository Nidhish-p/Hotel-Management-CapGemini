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
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.PaymentRepository;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    // ================= PAGE 1 TESTS =================

    @Test
    void getReservationByGuestName() {
        createReservation("tanvi@test.com", "Tanvi",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2));

        List<Reservation> list = reservationRepository.findByGuestNameContainingIgnoreCase("tanvi");

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }

    @Test
    void getReservationByGuestEmail() {
        createReservation("tanvi@test.com", "User",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2));

        List<Reservation> list = reservationRepository.findByGuestEmailContainingIgnoreCase("tanvi");

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }

    @Test
    void getReservationBetweenDates() {
        createReservation("test@test.com", "User",
                LocalDate.of(2024, 11, 10),
                LocalDate.of(2024, 11, 12));

        LocalDate start = LocalDate.of(2024, 10, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);

        List<Reservation> list = reservationRepository.findByCheckInDateBetween(start, end);

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }

    @Test
    void getReservationByCheckOutDate() {
        LocalDate date = LocalDate.of(2025, 3, 27);

        createReservation("co@test.com", "User",
                LocalDate.of(2025, 3, 20),
                date);

        List<Reservation> list = reservationRepository.findByCheckOutDate(date);

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }

    @Test
    void getReservationByCheckOutDateBetween() {
        createReservation("test@test.com", "User",
                LocalDate.of(2025, 3, 10),
                LocalDate.of(2025, 3, 20));

        List<Reservation> list = reservationRepository.findByCheckOutDateBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31));

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }


    @Test
    void getRoomByReservation() {
        Reservation r = createReservation("room@test.com", "Room",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 2));

        Room room = r.getRoom();
        assertThat(room).isNotNull();
    }

    @Test
    void getPaymentFromReservation() {
        Reservation r = createReservation("pay@test.com", "Pay",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 2));

        Payment payment = new Payment();
        payment.setAmount(500.0);
        payment.setPaymentStatus("PAID");
        payment.setPaymentDate(LocalDate.now());
        payment.setReservation(r);


        paymentRepository.save(payment);
        List<Payment> p = r.getPayments();
        p.add(payment);
        r.setPayments(p);
        reservationRepository.save(r);
        Reservation refreshed = reservationRepository.findById(r.getReservation_id()).orElseThrow();

        List<Payment> list = refreshed.getPayments();

        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }

    @Test
    void getHotelFromReservation() {
        Reservation r = createReservation("hotel@test.com", "Hotel",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 2));

        Hotel hotel = r.getRoom().getHotel();

        assertThat(hotel).isNotNull();
    }

    // ================= HELPER METHODS =================

    private Reservation createReservation(String email, String name,
            LocalDate checkIn, LocalDate checkOut) {

        Room room = createRoom();

        Reservation r = new Reservation();
        r.setGuestName(name);
        r.setGuestEmail(email);
        r.setGuest_phone("1234567890");
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);
        r.setRoom(room);

        return reservationRepository.save(r);
    }

    private Room createRoom() {

        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        RoomType roomType = new RoomType();
        roomType.setTypeName("Type-" + System.nanoTime());
        roomType.setDescription("Test type");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(java.math.BigDecimal.valueOf(999.99));
        roomType = roomTypeRepository.save(roomType);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        room.setHotel(hotel);

        return roomRepository.save(room);
    }


    //========DELETE CHECK ==========

@Test
void deleteReservationShouldAlsoDeletePayments() {

    Reservation r = createReservation("del@test.com", "Delete",
            LocalDate.of(2025, 8, 1),
            LocalDate.of(2025, 8, 3));

    Payment payment = new Payment();
    payment.setAmount(1000.0);
    payment.setPaymentStatus("PAID");
    payment.setPaymentDate(LocalDate.now());

    payment.setReservation(r);
    List<Payment> p = r.getPayments();
    p.add(payment);
    paymentRepository.save(payment);
    r.setPayments(p);
    reservationRepository.save(r);
    Integer reservationId = r.getReservation_id();

    assertThat(reservationRepository.findById(reservationId)).isPresent();

    reservationRepository.deleteById(reservationId);
    assertThat(reservationRepository.findById(reservationId)).isEmpty();

    List<Payment> remaining =
            paymentRepository.findAll()
                    .stream()
                    .filter(pay -> pay.getReservation() != null &&
                                 pay.getReservation().getReservation_id().equals(reservationId))
                    .toList();

    assertThat(remaining).isEmpty();
}


}

