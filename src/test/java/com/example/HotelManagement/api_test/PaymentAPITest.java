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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
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

//    @Autowired
//    private TestEntityManager entityManager;

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
        payment.setPayment_date(Date.valueOf(LocalDate.now()));
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
        payment.setPayment_date(Date.valueOf(LocalDate.now()));
        Payment saved = paymentRepository.save(payment);  // ✅ real ID generated here

        // Use the real DB-generated ID
        mockMvc.perform(get("/payments/{id}", saved.getPayment_id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1800.00))
                .andExpect(jsonPath("$.payment_status").value("SUCCESS"));
    }


//    @Test
//    public void testGetPaymentById() throws Exception {
//        mockMvc.perform(get("/payments/1")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

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
    void testPatchMultipleFields() throws Exception {
        Payment payment = new Payment();
        payment.setAmount(500.00);
        payment.setPayment_date(Date.valueOf("2024-01-15"));
        payment.setPayment_status("PENDING");
        Payment saved = paymentRepository.save(payment);

        Map<String, Object> patchData = new HashMap<>();
        patchData.put("amount", 1000.00);
        patchData.put("payment_status", "FAILED");
        patchData.put("payment_date", "2024-06-01");

        // Step 1: PATCH - just assert it succeeded
        mockMvc.perform(patch("/payments/" + saved.getPayment_id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchData)))
                .andExpect(status().isNoContent()); // ✅ 204 is correct

        // Step 2: GET - verify updated values
        mockMvc.perform(get("/payments/" + saved.getPayment_id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.payment_status").value("FAILED"));
    }

    @Test
    void shouldReturnPaymentsForValidHotelId() {
        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        assertNotNull(payments);
        assertFalse(payments.isEmpty());
        assertEquals(1, payments.size());
        assertEquals("PAID", payments.get(0).getPayment_status());
        assertEquals(299.99, payments.get(0).getAmount());
    }

    @Test
    void shouldReturnEmptyListForInvalidHotelId() {
        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(9999);

        assertNotNull(payments);
        assertTrue(payments.isEmpty());
    }

    // ✅ Test 3 - should return multiple payments for same hotel
    @Test
    void shouldReturnMultiplePaymentsForSameHotel() {
        // Add second payment for same reservation
        Payment payment2 = new Payment();
        payment2.setAmount(150.00);
        payment2.setPayment_status("PENDING");
        payment2.setPayment_date(Date.valueOf(LocalDate.now()));
        payment2.setReservation(reservation);
        entityManager.persist(payment2);
        entityManager.flush();

        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        assertEquals(2, payments.size());
    }

    // ✅ Test 4 - should not return payments from different hotel
    @Test
    void shouldNotReturnPaymentsFromDifferentHotel() {
        // Create another hotel with its own room, reservation, payment
        Hotel hotel2 = new Hotel();
        hotel2.setName("Another Hotel");
        hotel2.setLocation("Delhi");
        entityManager.persist(hotel2);

        Room room2 = new Room();
        room2.setRoomNumber(201);
        room2.setHotel(hotel2);
        room2.setIsAvailable(true);
        entityManager.persist(room2);

        Reservation reservation2 = new Reservation();
        reservation2.setGuestName("Jane Doe");
        reservation2.setGuestEmail("jane@gmail.com");
        reservation2.setGuest_phone("8888888888");
        reservation2.setRoom(room2);
        reservation2.setCheckInDate(java.time.LocalDate.now());
        reservation2.setCheckOutDate(java.time.LocalDate.now().plusDays(2));
        entityManager.persist(reservation2);

        Payment payment2 = new Payment();
        payment2.setAmount(500.00);
        payment2.setPayment_status("PAID");
        payment2.setPayment_date(Date.valueOf(LocalDate.now()));
        payment2.setReservation(reservation2);
        entityManager.persist(payment2);
        entityManager.flush();

        // Should only return payments for hotel 1
        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        assertEquals(1, payments.size());
        assertEquals(299.99, payments.get(0).getAmount());
    }

    // ✅ Test 5 - should return correct payment details
    @Test
    void shouldReturnCorrectPaymentDetails() {
        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        Payment result = payments.get(0);

        assertAll(
                () -> assertNotNull(result.getPayment_id()),
                () -> assertEquals(299.99, result.getAmount()),
                () -> assertEquals("PAID", result.getPayment_status()),
                () -> assertNotNull(result.getPayment_date()),
                () -> assertEquals(reservation.getReservation_id(),
                        result.getReservation().getReservation_id())
        );
    }
}