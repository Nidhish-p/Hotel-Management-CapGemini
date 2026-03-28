package com.example.HotelManagement.api_test;

import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
//import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.sql.Date;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PaymentAPITest {

    @Autowired
    private MockMvc mockMvc;

    private Payment testPayment;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = new Reservation();
        testReservation.setReservation_id(1);

        testPayment = new Payment();
        testPayment.setPayment_id(1);
        testPayment.setAmount(1500.00);
        testPayment.setPayment_date(Date.valueOf(LocalDate.now()));
        testPayment.setPayment_status("PAID");
        testPayment.setReservation(testReservation);
    }

    @Test
    public void testCreatePayment() throws Exception{
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
    public void testGetPaymentById() throws Exception{
        mockMvc.perform(get("/payments/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

//    @Test
//    public void testUpdatePaymentById() throws Exception{
//        String json = """
//                {
//                   "amount": 3000,
//                   "payment_date": "2026-03-23",
//                   "payment_status": "Paid",
//                   "reservation": "http://localhost:8081/reservations/1"
//                 }
//                """;
//        mockMvc.perform(put("/payments/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(json))
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
                """.formatted(testReservation.getReservation_id());

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


}
