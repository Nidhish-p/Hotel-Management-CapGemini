package com.example.HotelManagement.api_test;

import com.example.HotelManagement.entity.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.HotelManagement.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentAPITest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    private Hotel hotel;
    private Room room;
    private Reservation reservation;
    private Payment payment;

    @BeforeEach
    void setUp() {
        // Create Hotel
        hotel = new Hotel();
        hotel.setName("Grand Hotel");
        hotel.setLocation("Mumbai");
        hotel.setDescription("TEST 1 ");
        entityManager.persist(hotel);

        // Create RoomType
        RoomType roomType = new RoomType();
        roomType.setTypeName("Deluxe");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new BigDecimal("299.99"));
        entityManager.persist(roomType);

        // Create Room
        room = new Room();
        room.setRoomNumber(101);
        room.setHotel(hotel);
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        entityManager.persist(room);

        // Create Reservation
        reservation = new Reservation();
        reservation.setGuestName("John Doe");
        reservation.setGuestEmail("john@gmail.com");
        reservation.setGuest_phone("9999999999");
        reservation.setRoom(room);
        reservation.setCheckInDate(java.time.LocalDate.now());
        reservation.setCheckOutDate(java.time.LocalDate.now().plusDays(3));
        entityManager.persist(reservation);

        // Create Payment
        payment = new Payment();
        payment.setAmount(299.99);
        payment.setPayment_status("PAID");
        payment.setPayment_date(LocalDate.now());
        payment.setReservation(reservation);
        entityManager.persist(payment);

        entityManager.flush();
    }

    @Test
    public void testCreatePayment() throws Exception {
        String json = """
                {
                    "amount": 2377.00,
                    "payment_date": "2024-01-15",
                    "payment_status": "CD",
                    "reservation":"http://localhost:8081/reservations/1"
                }
                """;
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllPayments() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPaymentById() throws Exception {

        // Save to real DB
        Payment payment = new Payment();
        payment.setAmount(1800.00);
        payment.setPayment_status("SUCCESS");
        payment.setPayment_date(LocalDate.now());
        Payment saved = paymentRepository.save(payment);  // ✅ real ID generated here

        // Use the real DB-generated ID
        mockMvc.perform(get("/payments/{id}", saved.getPayment_id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1800.00))
                .andExpect(jsonPath("$.payment_status").value("SUCCESS"));
    }

    @Test
    public void testDeletePayment() throws Exception {
        String json = """
                {
                    "amount":12389,
                    "payment_date":"2026-03-23",
                    "payment_status":"Pending"
                }
                """;

        String location = mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreatePayment_InvalidData() throws Exception {
        String json = """
                {
                    "amount": -500.00,
                    "payment_date": "2026-03-28",
                    "payment_status": "FAILED",
                    "reservation": "/reservations/%d"
                }
                """.formatted(reservation.getReservation_id());

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test

    void shouldReturn200AndPaymentsForValidHotelId() throws Exception {
        mockMvc.perform(get("/payments/search/by-hotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").exists())
                .andExpect(jsonPath("$._embedded.payments", hasSize(1)))
                .andExpect(jsonPath("$._embedded.payments[0].amount").value(299.99))
                .andExpect(jsonPath("$._embedded.payments[0].paymentStatus").value("PAID"));
    }

    @Test
    void shouldReturnEmptyForInvalidHotelId() throws Exception {
        mockMvc.perform(get("/payments/search/by-hotel")
                        .param("hotelId", "9999")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isEmpty());
    }

    @Test
    void shouldReturnPaymentSummaryProjection() throws Exception {
        mockMvc.perform(get("/payments/search/by-hotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId()))
                        .param("projection", "paymentDetailsDTO")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$._embedded.payments[0].payment_id").exists())
                .andExpect(jsonPath("$._embedded.payments[0].amount").value(299.99))
                .andExpect(jsonPath("$._embedded.payments[0].paymentStatus").value("PAID"))
                // these fields should NOT be present in summary
                .andExpect(jsonPath("$._embedded.payments[0].guest_name").doesNotExist())
                .andExpect(jsonPath("$._embedded.payments[0].name").doesNotExist());
    }

    @Test
    void shouldReturnPaymentDetailsProjection() throws Exception {
        mockMvc.perform(get("/payments/search/by-hotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId()))
                        .param("projection", "paymentDetailsDTO")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments[0].amount").value(299.99))
                .andExpect(jsonPath("$._embedded.payments[0].guestName").value("John Doe"))
                .andExpect(jsonPath("$._embedded.payments[0].guestEmail").value("john@gmail.com"))
                // roomNumber and hotelName will need similar path corrections once you
                // confirm where they appear in the actual response body
                .andExpect(jsonPath("$._embedded.payments[0].roomNumber").value(101))
                .andExpect(jsonPath("$._embedded.payments[0].hotelName").value("Grand Hotel"));
    }

    @Test
    void shouldReturnMultiplePaymentsForSameHotel() throws Exception {
        Payment payment2 = new Payment();
        payment2.setAmount(150.00);
        payment2.setPayment_status("PENDING");
        payment2.setPayment_date(LocalDate.now());
        payment2.setReservation(reservation);
        entityManager.persist(payment2);
        entityManager.flush();

        mockMvc.perform(get("/payments/search/by-hotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments", hasSize(2)));
    }

    @Test
    void shouldReturn404ForWrongEndpoint() throws Exception {
        mockMvc.perform(get("/payments/search/wrong-endpoint")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn200WhenHotelIdIsMissing() throws Exception {
        mockMvc.perform(get("/payments/search/by-hotel")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); // ✅ FIX
    }
}