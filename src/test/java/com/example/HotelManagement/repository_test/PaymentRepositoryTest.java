package com.example.HotelManagement.repository_test;

import com.example.HotelManagement.entity.*;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.PaymentRepository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.example.HotelManagement.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final AtomicInteger RESERVATION_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 1_000_000);

    @Autowired
    private HotelRepository hotelRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Hotel hotel;
    private Room room;
    private Reservation reservation;
    private Payment payment;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // Create Hotel
        hotel = new Hotel();
        hotel.setName("Grand Hotel");
        hotel.setLocation("Mumbai");
        hotel.setDescription("TEST 1");
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
    public void testGetAllPayment(){
        assertNotNull(paymentRepository.findAll());
    }

    @Test
    public void testSavePayment(){
        Payment saved = paymentRepository.save(payment);

        assertNotNull(saved.getPayment_id());

    }

    @Test
    public void testDeletePayment() {
        paymentRepository.save(payment);
        paymentRepository.deleteById(payment.getPayment_id());
        Optional<Payment> result = paymentRepository.findById(payment.getPayment_id());
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetPaymentById() throws Exception{
        Payment saved = paymentRepository.save(payment);
        mockMvc.perform(get("/payments/" + saved.getPayment_id()))
                .andExpect(status().isOk());
    }

    @Test
    public void testSavePaymentWithInvalidReservationId(){
        Payment payment = new Payment();
        payment.setPayment_date(LocalDate.parse("2026-01-24"));
        payment.setAmount(9089.0);
        payment.setPayment_status("Paid");

        Reservation fakeReservation = new Reservation();
        fakeReservation.setReservation_id(9999);

        payment.setReservation(fakeReservation);

        assertThrows(Exception.class, () -> {
            paymentRepository.saveAndFlush(payment);
        });
    }

    @Test
    void testSavePayment_WithNullAmount() {
        Payment payment = new Payment();
        payment.setAmount(null);                               // Amount is null — test nullable behavior
        payment.setPayment_date(LocalDate.now());
        payment.setPayment_status("PENDING");
        payment.setReservation(testReservation);

        Payment saved = paymentRepository.save(payment);
        assertThat(saved.getAmount()).isNull();
    }

    @Test
    void testSavePayment_WithNullReservation() {
        Payment payment = new Payment();
        payment.setAmount(500.00);
        payment.setPayment_date(LocalDate.now());
        payment.setPayment_status("PENDING");
        payment.setReservation(null); // No reservation linked

        Payment saved = paymentRepository.save(payment);
        assertThat(saved.getReservation()).isNull();
    }

    @Test
    void testFindById_ExistingPayment() {
        entityManager.persist(payment);
        entityManager.flush();

        Payment persisted = payment;

        Optional<Payment> found = paymentRepository.findById(persisted.getPayment_id());

        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualTo(299.99);
        assertThat(found.get().getPayment_status()).isEqualTo("PAID");
    }

    @Test
    void testPatchMultipleFields() throws Exception {
        Payment payment = new Payment();
        payment.setAmount(500.00);
        payment.setPayment_date(LocalDate.parse("2024-01-15"));
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

    @Test
    void shouldReturnMultiplePaymentsForSameHotel() {
        // Add second payment for same reservation
        Payment payment2 = new Payment();
        payment2.setAmount(150.00);
        payment2.setPayment_status("PENDING");
        payment2.setPayment_date(LocalDate.now());
        payment2.setReservation(reservation);
        entityManager.persist(payment2);
        entityManager.flush();

        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        assertEquals(2, payments.size());
    }

    @Test
    void shouldNotReturnPaymentsFromDifferentHotel() {
        // Create another hotel with its own room, reservation, payment
        Hotel hotel2 = new Hotel();
        hotel2.setName("Another Hotel");
        hotel2.setDescription("TEST 2");
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
        payment2.setPayment_date(LocalDate.now());
        payment2.setReservation(reservation2);
        entityManager.persist(payment2);
        entityManager.flush();

        // Should only return payments for hotel 1
        List<Payment> payments = paymentRepository
                .findByReservation_Room_Hotel_HotelId(hotel.getHotelId());

        assertEquals(1, payments.size());
        assertEquals(299.99, payments.get(0).getAmount());
    }

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
