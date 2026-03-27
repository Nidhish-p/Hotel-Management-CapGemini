package com.example.HotelManagement.api_test;

import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.RoomRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewApiTest {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepo roomRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MockMvc mockMvc;

    // TEST 1: SAVE REVIEW
    @Test
    void testSaveReview() throws Exception {

        String reviewJson = """
                {
                    "review_date": "2026-03-25",
                    "rating": 5,
                    "comment": "Excellent stay!"
                }
                """;

        mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isCreated());
    }

    @Test
    void testSaveReviewWithInvalidReservationAPI() throws Exception {
        String reviewJson = """
                {
                    "review_date": "2026-03-25",
                    "rating": 4,
                    "comment": "Invalid FK API test",
                    "reservation": "http://localhost:8080/reservations/9999"
                }
                """;

        String location = mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andExpect(status().isCreated()) // ✅ EXPECT SUCCESS
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Verify reservation is NULL
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservation").doesNotExist());
    }

    // TEST 2: GET ALL REVIEWS
    @Test
    void testGetAllReviews() throws Exception {

        mockMvc.perform(get("/review"))
                .andExpect(status().isOk());

    }
    @Test
    void testCreateReview_InvalidRating_ShouldReturn400() throws  Exception{

        // 1️⃣ Create Reservation
        Reservation reservation = new Reservation();
        reservation = reservationRepository.save(reservation);

        // 2️⃣ Invalid request body
        String requestBody = """
        {
            "review_date": "2026-03-27",
            "rating": 10,
            "comment": "Bad rating test",
            "reservation": "/reservations/""" + reservation.getReservation_id() + """
        }
        """;

        // 3️⃣ Perform request using MockMvc
        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }






    @Test
    void testUpdateReview() throws Exception {

        String reviewJson = """
                {
                    "review_date": "2026-03-25",
                    "rating": 3,
                    "comment": "Old comment"
                }
                """;

        // Creating review
        String response = mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Extracting id from url
        String id = response.substring(response.lastIndexOf("/") + 1);

        // Update review
        String updatedJson = """
                {
                    "review_date": "2026-03-25",
                    "rating": 5,
                    "comment": "Updated comment"
                }
                """;

        mockMvc.perform(put("/review/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isNoContent());

        // Verify update
        mockMvc.perform(get("/review/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }
    @Test
    void getReviewsByHotelId_shouldContainInsertedReview() throws Exception {

        // 1️⃣ Create Hotel (NO manual ID)
        Hotel hotel = new Hotel();
        hotel.setName("API Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        Integer hotelId = hotel.getHotelId();

        // 2️⃣ Room
        Room room = new Room();
        room.setRoomNumber(1234);
        room.setRoomTypeId(1);
        room.setIsAvailable(true);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        // 3️⃣ Reservation
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation = reservationRepository.save(reservation);

        // 4️⃣ Review
        Review review = new Review();
        review.setRating(4);
        review.setComment("API Test Review");
        review.setReview_date(LocalDate.now());
        review.setReservation(reservation);

        Review savedReview = reviewRepository.save(review); // 🔥 important
        mockMvc.perform(get("/review/search/findDistinctByReservationRoomHotelHotelId")
                        .param("hotelId", hotelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews[*].comment")
                        .value(org.hamcrest.Matchers.hasItem("API Test Review")));
    }

}