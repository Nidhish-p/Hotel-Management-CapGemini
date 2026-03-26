package com.example.HotelManagement.repository_test;
import com.example.HotelManagement.entity.Review;
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

    //  TEST 1: SAVE REVIEW
    @Test
    void testSaveReview() {

        Review review = new Review();
        review.setReview_date(LocalDate.of(2026, 3, 25));
        review.setRating(5);
        review.setComment("Excellent stay!");

        Review saved = reviewRepository.save(review);

        assertNotNull(saved.getReview_id());
        assertEquals(5, saved.getRating());
    }

    // TEST 2: FIND ALL REVIEWS
    @Test
    void testFindAllReviews() {
        long initialCount = reviewRepository.count();

        Review review1 = new Review();
        review1.setReview_date(LocalDate.now());
        review1.setRating(4);
        review1.setComment("Good");

        Review review2 = new Review();
        review2.setReview_date(LocalDate.now());
        review2.setRating(3);
        review2.setComment("Average");

        reviewRepository.save(review1);
        reviewRepository.save(review2);

        long finalCount = reviewRepository.count();

        assertEquals(initialCount + 2, finalCount);
    }

}
