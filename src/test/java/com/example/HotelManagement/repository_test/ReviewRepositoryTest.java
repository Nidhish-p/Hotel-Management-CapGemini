package com.example.HotelManagement.repository_test;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private EntityManager entityManager;

    private static final AtomicInteger RESERVATION_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 1_000_000);

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

    private Reservation insertReservation() {
        int id = RESERVATION_SEQ.getAndIncrement();
        entityManager.createNativeQuery(
                        "insert into reservation (reservation_id, check_in_date, check_out_date, guest_email, guest_name, guest_phone, room_id) " +
                                "values (?,?,?,?,?,?,?)")
                .setParameter(1, id)
                .setParameter(2, LocalDate.of(2026, 3, 20))
                .setParameter(3, LocalDate.of(2026, 3, 25))
                .setParameter(4, "zaid@email.com")
                .setParameter(5, "Zaid")
                .setParameter(6, "9876543210")
                .setParameter(7, null)
                .executeUpdate();
        return entityManager.getReference(Reservation.class, id);
    }

}
