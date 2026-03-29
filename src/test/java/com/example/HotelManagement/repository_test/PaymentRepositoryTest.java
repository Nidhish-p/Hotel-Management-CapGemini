package com.example.HotelManagement.repository_test;

import com.example.HotelManagement.api_test.PaymentAPITest;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.PaymentRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.example.HotelManagement.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final AtomicInteger RESERVATION_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 1_000_000);


    private Reservation testReservation;
    private Payment testPayment;
    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        entityManager.persist(testReservation);
        testPayment = new Payment();
        testPayment.setAmount(1500.00);
        testPayment.setPayment_date(Date.valueOf(LocalDate.now()));
        testPayment.setPayment_status("PAID");
        testPayment.setReservation(testReservation);

        entityManager.flush();
    }

    @Test
    @Transactional
    public void testGetAllPayment(){
        assertNotNull(paymentRepository.findAll());
    }

    @Test
    @Transactional
    public void testSavePayment(){
        Payment saved = paymentRepository.save(testPayment);

        assertNotNull(saved.getPayment_id());

    }

    @Test
    @Transactional
    public void testDeletePayment() {
        paymentRepository.save(testPayment);
        paymentRepository.deleteById(testPayment.getPayment_id());
        Optional<Payment> result = paymentRepository.findById(testPayment.getPayment_id());
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    public void testGetPaymentById() throws Exception{
        Payment saved = paymentRepository.save(testPayment);
        mockMvc.perform(get("/payments/" + saved.getPayment_id()))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testSavePaymentWithInvalidReservationId(){
        Payment payment = new Payment();
        payment.setPayment_date(Date.valueOf("2026-01-24"));
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
    @Transactional
    void testSavePayment_WithNullAmount() {
        Payment payment = new Payment();
        payment.setAmount(null);                               // Amount is null — test nullable behavior
        payment.setPayment_date(Date.valueOf(LocalDate.now()));
        payment.setPayment_status("PENDING");
        payment.setReservation(testReservation);

        Payment saved = paymentRepository.save(payment);
        assertThat(saved.getAmount()).isNull();
    }

    @Test
    @Transactional
    void testSavePayment_WithNullReservation() {
        Payment payment = new Payment();
        payment.setAmount(500.00);
        payment.setPayment_date(Date.valueOf(LocalDate.now()));
        payment.setPayment_status("PENDING");
        payment.setReservation(null); // No reservation linked

        Payment saved = paymentRepository.save(payment);
        assertThat(saved.getReservation()).isNull();
    }

    @Test
    @Transactional
    void testFindById_ExistingPayment() {
        entityManager.persist(testPayment);
        entityManager.flush();

        Payment persisted = testPayment;

        Optional<Payment> found = paymentRepository.findById(persisted.getPayment_id());

        assertThat(found).isPresent();
        assertThat(found.get().getAmount()).isEqualTo(1500.00);
        assertThat(found.get().getPayment_status()).isEqualTo("PAID");
    }

//    test for negative amount


}
