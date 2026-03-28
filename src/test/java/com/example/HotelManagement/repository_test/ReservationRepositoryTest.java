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
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.PaymentRepository;

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

    //2nd page

    @Test
    void getAllReservationsTestRepo() {
        createReservation("user1@test.com", "User1", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2));
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty();
    }

    @Test
    void getReservationByGuestEmail() {
        String email = "tanvi@test.com";
        createReservation(email, "Tanvi", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2));

        List<Reservation> list = reservationRepository.findByGuestEmail(email);

        assertThat(list).isNotNull();
        assertThat(list).allMatch(r -> r.getGuestEmail().equals(email));
    }

    @Test
    void getReservationByCheckInDate() {
        LocalDate date = LocalDate.of(2024, 11, 1); // match your data.sql
        createReservation("ci@test.com", "CI", date, LocalDate.of(2024, 11, 3));
        List<Reservation> list = reservationRepository.findByCheckInDate(date);
        assertThat(list).isNotNull();
        assertThat(list).isNotEmpty();
    }
    
    @Test
    void getReservationByGuestName() {
        String name = "Tanvi"; 
        createReservation("gn@test.com", name, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 3));

        List<Reservation> list = reservationRepository.findByGuestName(name);

        assertThat(list).isNotNull();
        assertThat(list).allMatch(r -> r.getGuestName().equals(name));
    }
    
    @Test
    void getReservationBetweenDates() {
        LocalDate start = LocalDate.of(2024, 10, 1);
        LocalDate end = LocalDate.of(2024, 10, 5);
        createReservation("bd@test.com", "Between", LocalDate.of(2024, 10, 2), LocalDate.of(2024, 10, 4));

        List<Reservation> list = reservationRepository.findByCheckInDateBetween(start, end);

        assertThat(list).isNotNull();
        assertThat(list.size()).isGreaterThan(0);
    }
    
    @Test
    void getReservationByCheckOutDate() {
        LocalDate date = LocalDate.of(2025, 3, 27);
        createReservation("co@test.com", "CO", LocalDate.of(2025, 3, 20), date);

        List<Reservation> list = reservationRepository.findByCheckOutDate(date);

        assertThat(list).isNotNull();
    }

    @Test
    void addReservation() {
        Room room = createRoom();

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
        Reservation r = createReservation("room@test.com", "Room", LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
        Room room = r.getRoom();
        assertThat(room).isNotNull();
    }

    @Test
    void getReviewFromReservation() {
        Reservation r = createReservation("rev@test.com", "Rev", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 2));
        Review review = new Review();
        review.setRating(4);
        review.setComment("Good");
        review.setReview_date(LocalDate.now());
        review.setReservation(r);
        reviewRepository.save(review);
        Reservation refreshed = reservationRepository.findById(r.getReservation_id()).orElseThrow();
        List<Review> list = refreshed.getReviews();
        assertThat(list).isNotNull();
    }
    
    @Test
    void getPaymentFromReservation() {
        Reservation r = createReservation("pay@test.com", "Pay", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 2));
        Payment payment = new Payment();
        payment.setAmount(500.0);
        payment.setPayment_status("PAID");
        payment.setPayment_date(new java.sql.Date(System.currentTimeMillis()));
        payment.setReservation(r);
        paymentRepository.save(payment);
        Reservation refreshed = reservationRepository.findById(r.getReservation_id()).orElseThrow();
        List<Payment> list = refreshed.getPayments();
        assertThat(list).isNotNull();
    }

    @Test
    void getHotelFromReservation() {
        Reservation r = createReservation("hotel@test.com", "Hotel", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 2));
        Hotel hotel = r.getRoom().getHotel();
        assertThat(hotel).isNotNull();
    }

    private Reservation createReservation(String email, String name, LocalDate checkIn, LocalDate checkOut) {
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
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);
        room.setHotel(hotel);
        return roomRepository.save(room);
    }
    
}
