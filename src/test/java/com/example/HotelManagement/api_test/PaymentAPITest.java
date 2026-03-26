package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentAPITest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPaymentById() throws Exception{
        mockMvc.perform(get("/payments/1")).andExpect(status().isOk());
    }
}
