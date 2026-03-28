package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ReservationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllReservationsTestApi() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getRoomByReservationApi() throws Exception {
        mockMvc.perform(get("/reservations/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.room").exists()); // ✅ FIX
    }

    @Test
    void getReviewFromReservationApi() throws Exception {
        mockMvc.perform(get("/reservations/1/review"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviews").isArray());
    }

    @Test
    void getPaymentFromReservationApi() throws Exception {
        mockMvc.perform(get("/reservations/1/payments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.payments").isArray()); 
    }

    @Test
    void getReservationByGuestEmailApi() throws Exception {
        mockMvc.perform(get("/reservations/search/findByGuestEmail?email=tanvi@test.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByCheckInDateApi() throws Exception {
        mockMvc.perform(get("/reservations/search/findByCheckInDate?date=2024-11-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationBetweenDatesApi() throws Exception {
        mockMvc.perform(get("/reservations/search/findByCheckInDateBetween")
                .param("start", "2024-10-01")
                .param("end", "2024-10-05"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByGuestNameApi() throws Exception {
        mockMvc.perform(get("/reservations/search/findByGuestName?name=Tanvi"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getReservationByCheckOutDateApi() throws Exception {
        mockMvc.perform(get("/reservations/search/findByCheckOutDate?date=2025-03-27"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservations").isArray());
    }

    @Test
    void getHotelFromReservationViaRoom() throws Exception {
    MvcResult result = mockMvc.perform(get("/reservations/1/room"))
            .andExpect(status().isOk())
            .andReturn();

    String response = result.getResponse().getContentAsString();

    mockMvc.perform(get("/rooms/50/hotel"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Hotel"))
            .andExpect(jsonPath("$.location").value("Test City"))
            .andExpect(jsonPath("$.description").exists());
    }

}

