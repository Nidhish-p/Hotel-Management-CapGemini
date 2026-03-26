package com.example.HotelManagement.repository_test;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    //  TEST 1: SAVE REVIEW
    @Test
    void testSaveReview() {
        // Step 1: Create Reservation
        Reservation reservation = new Reservation();
        reservation.setGuest_name("Zaid");
        reservation.setGuest_email("zaid@email.com");
        reservation.setGuest_phone("9876543210");
        reservation.setCheck_in_date(LocalDate.of(2026, 3, 20));
        reservation.setCheck_out_date(LocalDate.of(2026, 3, 25));
        Reservation savedReservation = reservationRepository.save(reservation);

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
    void testFindAllReviews() {
        long initialCount = reviewRepository.count();
        Reservation reservation = new Reservation();
        reservation.setGuest_name("Zaid");
        reservation.setGuest_email("zaid@email.com");
        reservation.setGuest_phone("9876543210");
        reservation.setCheck_in_date(LocalDate.of(2026, 3, 20));
        reservation.setCheck_out_date(LocalDate.of(2026, 3, 25));
        Reservation savedReservation = reservationRepository.save(reservation);

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

}
