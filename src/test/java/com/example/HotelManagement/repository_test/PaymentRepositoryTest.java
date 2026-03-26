package com.example.HotelManagement.repository_test;

import com.example.HotelManagement.api_test.PaymentAPITest;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.repository.PaymentRepository;
import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.repository.PaymentRepository;


@SpringBootTest
public class PaymentRepositoryTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testGetAllPayment(){
        List<Payment> list = paymentRepository.findAll();
        System.out.println(list);

    }

    @Test
    public void testSavePayment(){
        Payment payment = new Payment();
        payment.setPayment_date(Date.valueOf("2026-01-24"));
        payment.setAmount(9089.0);
        payment.setPayment_status("Paid");

        Payment saved = paymentRepository.save(payment);

        assertNotNull(saved.getPayment_id());
        assertTrue(
                (saved.getPayment_status().toLowerCase().equals("unpaid") ||
                        saved.getPayment_status().toLowerCase().equals("paid") ||
                        saved.getPayment_status().toLowerCase().equals("failed")) &&
                        saved.getAmount()>0
        );
    }

    @Test
    public void testDeletePayment(){
        Payment payment = new Payment();
        payment.setPayment_date(Date.valueOf("2026-01-24"));
        payment.setAmount(9089.0);
        payment.setPayment_status("Paid");

        paymentRepository.save(payment);

        paymentRepository.deleteById(payment.getPayment_id());

        Optional<Payment> result = paymentRepository.findById(payment.getPayment_id());
        assertFalse(result.isPresent());

    public void testGetPaymentById() throws Exception{
        Payment payment = new Payment();
        payment.setAmount(100.0);
        payment.setPayment_date(Date.valueOf("2026-04-01"));
        payment.setPayment_status("PAID");

        Payment saved = paymentRepository.save(payment);
        mockMvc.perform(get("/payments/" + saved.getPayment_id()))
                .andExpect(status().isOk());
    }

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
