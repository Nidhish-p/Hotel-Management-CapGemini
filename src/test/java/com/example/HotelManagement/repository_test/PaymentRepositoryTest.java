package com.example.HotelManagement.repository_test;

import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentRepositoryTest {

    @Autowired
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
        mockMvc.perform(get("/payments/1")).andExpect(status().isOk());
    }

}
