package com.example.HotelManagement.controller;

import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.repository.ReviewRepository;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/custom-review")
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewDetails(@PathVariable Integer id) {

        Optional<Review> optionalReview = reviewRepository.findById(id);

        if (optionalReview.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review not found");
        }

        Review review = optionalReview.get();
        Reservation res = review.getReservation();
        Room room = res.getRoom();

        Map<String, Object> response = new HashMap<>();

        // 🔹 Review
        response.put("reviewId", id);
        response.put("comment", review.getComment());
        response.put("rating", review.getRating());
        response.put("reviewDate", review.getReview_date());

        // 🔹 Reservation
        response.put("guestName", res.getGuestName());
        response.put("checkInDate", res.getCheckInDate());
        response.put("checkOutDate", res.getCheckOutDate());
        response.put("reservationId", res.getReservation_id());

        // 🔹 Room
        response.put("roomNumber", room.getRoomNumber());

        // 🔹 RoomType
        if (room.getRoomType() != null) {
            response.put("roomType", room.getRoomType().getTypeName());
        } else {
            response.put("roomType", null);
        }

        return ResponseEntity.ok(response);
    }
}
