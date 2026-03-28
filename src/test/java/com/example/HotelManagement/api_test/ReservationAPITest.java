package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.PaymentRepository;
import com.example.HotelManagement.repository.ReservationRepository;
import com.example.HotelManagement.repository.ReviewRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationApiTest {

    @Autowired
    private MockMvc mockMvc;
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

    @Test
    void getAllReservationsTestApi() throws Exception {
        createReservation("api1@test.com", "API1", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2));
        mockMvc.perform(get("/reservations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getRoomByReservationApi() throws Exception {
        Reservation reservation = createReservation("api2@test.com", "API2", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2));
        mockMvc.perform(get("/reservations/" + reservation.getReservation_id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.room").exists()); // ✅ FIX
    }

    @Test
    void getReviewFromReservationApi() throws Exception {
        Reservation reservation = createReservation("api3@test.com", "API3", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 2));
        Review review = new Review();
        review.setRating(4);
        review.setComment("Good");
        review.setReview_date(LocalDate.now());
        review.setReservation(reservation);
        reviewRepository.save(review);

        mockMvc.perform(get("/reservations/" + reservation.getReservation_id() + "/reviews"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews").isArray());
    }

    @Test
    void getPaymentFromReservationApi() throws Exception {
        Reservation reservation = createReservation("api4@test.com", "API4", LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 2));
        Payment payment = new Payment();
        payment.setAmount(500.0);
        payment.setPayment_status("PAID");
        payment.setPayment_date(new java.sql.Date(System.currentTimeMillis()));
        payment.setReservation(reservation);
        paymentRepository.save(payment);

        mockMvc.perform(get("/reservations/" + reservation.getReservation_id() + "/payments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isArray()); 
    }

    @Test
    void getReservationByGuestEmailApi() throws Exception {
        createReservation("tanvi@test.com", "Tanvi", LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 2));
        mockMvc.perform(get("/reservations/search/findByGuestEmail?email=tanvi@test.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByCheckInDateApi() throws Exception {
        createReservation("ci@test.com", "CI", LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 3));
        mockMvc.perform(get("/reservations/search/findByCheckInDate?date=2024-11-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationBetweenDatesApi() throws Exception {
        createReservation("bd@test.com", "Between", LocalDate.of(2024, 10, 2), LocalDate.of(2024, 10, 4));
        mockMvc.perform(get("/reservations/search/findByCheckInDateBetween")
                .param("start", "2024-10-01")
                .param("end", "2024-10-05"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByGuestNameApi() throws Exception {
        createReservation("gn@test.com", "Tanvi", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 2));
        mockMvc.perform(get("/reservations/search/findByGuestName?name=Tanvi"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByCheckOutDateApi() throws Exception {
        createReservation("co@test.com", "CO", LocalDate.of(2025, 3, 20), LocalDate.of(2025, 3, 27));
        mockMvc.perform(get("/reservations/search/findByCheckOutDate?date=2025-03-27"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getHotelFromReservationViaRoom() throws Exception {
        Reservation reservation = createReservation("hotel@test.com", "Hotel", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 2));
        Room room = reservation.getRoom();

        mockMvc.perform(get("/rooms/" + room.getRoomId() + "/hotel"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Hotel"))
                .andExpect(jsonPath("$.location").value("Test City"))
                .andExpect(jsonPath("$.description").exists());
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
        hotel.setLocation("Test City");
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

