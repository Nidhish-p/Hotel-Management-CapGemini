package com.example.HotelManagement.repository_test;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.sql.Date;

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
