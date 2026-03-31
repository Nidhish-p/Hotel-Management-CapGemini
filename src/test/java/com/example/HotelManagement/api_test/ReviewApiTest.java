package com.example.HotelManagement.api_test;

import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;
import com.example.HotelManagement.entity.RoomType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback
public class ReviewApiTest {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // TEST 1: SAVE REVIEW
    @Test
    void testSaveReview() throws Exception {

        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        // RoomType
//        RoomType roomType = new RoomType();
//        roomType.setTypeName("Deluxe");
//        roomType = roomTypeRepository.save(roomType);

        RoomType roomType = new RoomType();
        roomType.setTypeName("Deluxe");
        roomType.setDescription("Deluxe room");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(BigDecimal.valueOf(2000));
        roomType = roomTypeRepository.save(roomType);

        // Room
        Room room = new Room();
        room.setRoomNumber(101);
        room.setIsAvailable(true);
        room.setHotel(hotel);
        room.setRoomType(roomType);
        room = roomRepository.save(room);

        // Reservation
        Reservation reservation = new Reservation();
        reservation.setGuestName("Zaid");
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(2));
        reservation.setRoom(room);
        reservation = reservationRepository.save(reservation);

        //  Create

        String reviewJson = """
            {
                "review_date": "2026-03-25",
                "rating": 5,
                "comment": "Excellent stay!",
                "reservation": "/reservations/%d"
            }
            """.formatted(reservation.getReservation_id());

        // 3️⃣ API Call
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
                .andExpect(status().isCreated())
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
        RoomType roomType = createRoomType("API");
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
        review.setRating(4);
        review.setComment("API Test Review");
        review.setReview_date(LocalDate.now());
        review.setReservation(reservation);

        Review savedReview = reviewRepository.save(review); // important
        mockMvc.perform(get("/review/search/findDistinctByReservationRoomHotelHotelId")
                        .param("hotelId", hotelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews[*].comment")
                        .value(org.hamcrest.Matchers.hasItem("API Test Review")));
    }

    @Test
    void testGetReviewsWithPagination() throws Exception {

        // Insert some reviews (no assumption about DB state)
        for (int i = 1; i <= 10; i++) {
            Review review = new Review();
            review.setRating(i % 5);
            review.setComment("API Review " + i);
            review.setReview_date(LocalDate.now());

            reviewRepository.save(review);
        }

        reviewRepository.flush();

        mockMvc.perform(get("/review")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$._embedded.reviews.length()").value(5))
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }
    @Test
    void testGetReviewsSecondPage() throws Exception {

        for (int i = 1; i <= 10; i++) {
            Review review = new Review();
            review.setRating(i % 5);
            review.setComment("Page2 Review " + i);
            review.setReview_date(LocalDate.now());

            reviewRepository.save(review);
        }

        reviewRepository.flush();

        mockMvc.perform(get("/review")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews.length()").value(5))
                .andExpect(jsonPath("$.page.number").value(1));
    }
    @Test
    void testGetReviewsEmptyPage() throws Exception {

        mockMvc.perform(get("/review")
                        .param("page", "999")
                        .param("size", "5"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$._embedded.reviews").isArray())
                .andExpect(jsonPath("$._embedded.reviews.length()").value(0));
    }
    @Test
    void getReviewDetailsById_shouldReturnProjection() throws Exception {

        // 1️⃣ Hotel
        Hotel hotel = new Hotel();
        hotel.setName("API Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        // 2️⃣ RoomType
        RoomType roomType = createRoomType("Deluxe");

        // 3️⃣ Room
        Room room = new Room();
        room.setRoomNumber(202);
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        // 4️⃣ Reservation
        Reservation reservation = new Reservation();
        reservation.setGuestName("API User");
        reservation.setGuestEmail("api@test.com");
        reservation.setGuest_phone("8888888888");
        reservation.setCheckInDate(LocalDate.of(2026, 4, 1));
        reservation.setCheckOutDate(LocalDate.of(2026, 4, 5));
        reservation.setRoom(room);
        reservation = reservationRepository.save(reservation);

        // 5️⃣ Review
        Review review = new Review();
        review.setRating(4);
        review.setComment("API Projection Test");
        review.setReview_date(LocalDate.now());
        review.setReservation(reservation);
        review = reviewRepository.save(review);

        // 6️⃣ Call API
        mockMvc.perform(get("/review/" + review.getReview_id())
                        .param("projection", "reviewDetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("API Projection Test"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$._embedded.reservation.guestName").value("API User"));
    }
    @Test
    void testGetReviewsDefaultPagination() throws Exception {

        mockMvc.perform(get("/review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews").exists())
                .andExpect(jsonPath("$.page").exists());
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
