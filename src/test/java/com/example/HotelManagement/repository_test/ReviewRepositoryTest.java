package com.example.HotelManagement.repository_test;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@Rollback
@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    //  TEST 1: SAVE REVIEW
    @Test
    @Transactional
    void testSaveReview() {
        // Step 1: Create Reservation
        Reservation savedReservation = insertReservation();

        // Step 2: Create Review
        Review review = new Review();
        review.setReview_date(LocalDate.of(2026, 3, 25));
        review.setRating(5);
        review.setComment("Excellent stay!");
        review.setReservation(savedReservation);  // ✅ FK set

        // Step 3: Save Review
        Review saved = reviewRepository.save(review);

        // Step 4: Assertions
        assertNotNull(saved.getReview_id());
        assertEquals(5, saved.getRating());
        assertEquals(savedReservation.getReservation_id(),
                saved.getReservation().getReservation_id());
    }

    // TEST 2: FIND ALL REVIEWS
    @Test
    @Transactional
    void testFindAllReviews() {
        long initialCount = reviewRepository.count();
        Reservation savedReservation = insertReservation();

        Review review1 = new Review();
        review1.setReview_date(LocalDate.now());
        review1.setRating(4);
        review1.setComment("Good");
        review1.setReservation(savedReservation);
        Review review2 = new Review();
        review2.setReview_date(LocalDate.now());
        review2.setRating(3);
        review2.setComment("Average");
        review2.setReservation(savedReservation);
        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long finalCount = reviewRepository.count();

        assertEquals(initialCount + 2, finalCount);
    }
    @Test
    void testSaveReviewWithInvalidReservation() {

        Review review = new Review();
        review.setReview_date(LocalDate.of(2026, 3, 25));
        review.setRating(4);
        review.setComment("Invalid FK test");

        // Fake reservation (not saved in DB)
        Reservation fakeReservation = new Reservation();
        fakeReservation.setReservation_id(9999); // ❌ does not exist

        review.setReservation(fakeReservation);

        // Expect FK violation
        assertThrows(Exception.class, () -> {
            reviewRepository.saveAndFlush(review); // force DB check
        });

    }

    @Test
    void saveReview_InvalidRating_ShouldThrowException() {

        // 1️⃣ Create reservation (required)
        Reservation reservation = insertReservation();

        // 2️⃣ Create invalid review
        Review review = new Review();
        review.setReview_date(LocalDate.now());
        review.setRating(10); // ❌ invalid
        review.setComment("Invalid rating test");
        review.setReservation(reservation);

        // 3️⃣ Expect exception
        assertThrows(ConstraintViolationException.class, () -> {
            reviewRepository.save(review);
        });
    }


    private Reservation insertReservation() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        RoomType roomType = createRoomType("Standard");

        Room room = new Room();
        room.setRoomNumber(101);
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        Reservation reservation = new Reservation();
        reservation.setGuestName("Zaid");
        reservation.setGuestEmail("zaid@email.com");
        reservation.setGuest_phone("9876543210");
        reservation.setCheckInDate(LocalDate.of(2026, 3, 20));
        reservation.setCheckOutDate(LocalDate.of(2026, 3, 25));
        reservation.setRoom(room);
        return reservationRepository.save(reservation);
    }

    @Test
    void updateReview_shouldUpdateRatingAndComment() {

        // 1️⃣ Create Reservation (required)
        Reservation reservation = insertReservation();

        // 2️⃣ Create Review
        Review review = new Review();
        review.setReview_date(LocalDate.now());
        review.setRating(3);
        review.setComment("Average stay");
        review.setReservation(reservation);

        Review savedReview = reviewRepository.save(review);

        // 3️⃣ Update fields
        savedReview.setRating(5);
        savedReview.setComment("Excellent stay!");

        reviewRepository.save(savedReview);

        // 4️⃣ Fetch again
        Review updated = reviewRepository.findById(savedReview.getReview_id()).orElse(null);

        // 5️⃣ Assertions
        assertNotNull(updated);
        assertEquals(5, updated.getRating());
        assertEquals("Excellent stay!", updated.getComment());
    }
    @Test
    void findReviewsByHotelId_shouldContainInsertedReview() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        Integer hotelId = hotel.getHotelId(); // 🔥 IMPORTANT

        // 2️⃣ Room
        Room room = new Room();
        room.setRoomNumber(101);
        RoomType roomType = createRoomType("Suite");
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        // 3️⃣ Reservation
        Reservation reservation = new Reservation();
        reservation.setGuestName("Guest");
        reservation.setGuestEmail("guest@example.com");
        reservation.setGuest_phone("9876543210");
        reservation.setCheckInDate(LocalDate.of(2026, 3, 20));
        reservation.setCheckOutDate(LocalDate.of(2026, 3, 25));
        reservation.setRoom(room);
        reservation = reservationRepository.save(reservation);

        // 4️⃣ Review
        Review review = new Review();
        review.setRating(5);
        review.setComment("Unique Test");
        review.setReview_date(LocalDate.now());
        review.setReservation(reservation);
        review = reviewRepository.save(review);

        // 5️⃣ Query
        List<Review> result =
                reviewRepository.findDistinctByReservationRoomHotelHotelId(hotelId);


        Review savedReview = reviewRepository.save(review); // ✅ new variable

        assertTrue(result.stream()
                .anyMatch(r -> r.getReview_id().equals(savedReview.getReview_id())));
    }
    @Test
    void findReviewsByHotelId_shouldReturnEmpty_whenNoMatch() {

        List<Review> result =
                reviewRepository.findDistinctByReservationRoomHotelHotelId(999);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private RoomType createRoomType(String name) {
        RoomType roomType = new RoomType();
        roomType.setTypeName(name + "-" + System.nanoTime());
        roomType.setDescription("Test type");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(java.math.BigDecimal.valueOf(999.99));
        return roomTypeRepository.save(roomType);
    }




}
