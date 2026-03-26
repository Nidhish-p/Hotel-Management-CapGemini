package com.example.HotelManagement.api_test;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetPaymentById() throws Exception{
        mockMvc.perform(get("/payments/1")).andExpect(status().isOk());
    }

    @Test
    public void testCreatePayment() throws Exception{
        String json = """
                {
                    "amount":12389,
                    "payment_date":"2026-03-23",
                    "payment_status":"Pending"
                }
                """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"));

    }

    @Test
    public void testGetAllPayments() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());
    }

//    @Test
//    public void testUpdatePayment() throws Exception {
//
//        String json = """
//                {
//                    "reservationId":1,
//                    "amount":3000,
//                    "paymentDate":"2026-03-23",
//                    "paymentStatus":"Completed"
//                }
//                """;
//
//        mockMvc.perform(put("/payments/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk());
//    }

    @Test
    public void testDeletePayment() throws Exception {
        mockMvc.perform(delete("/payments/1"))
                .andExpect(status().isNoContent());
    }

}
