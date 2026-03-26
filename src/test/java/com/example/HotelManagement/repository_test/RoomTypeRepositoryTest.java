package com.example.HotelManagement.repository_test;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RoomTypeRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @BeforeEach
    void setup() {
        roomTypeRepository.deleteAll();

        RoomType single = new RoomType("Single", "Single room description", 1, new BigDecimal("100.00"));
        RoomType doubleRoom = new RoomType("Double", "Double room description", 2, new BigDecimal("200.00"));

        roomTypeRepository.saveAll(List.of(single, doubleRoom));
    }

    @Test
    void testGetAllRoomTypes_Returns200() throws Exception {
        mockMvc.perform(get("/roomtypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllRoomTypes_HasEmbedded() throws Exception {
        mockMvc.perform(get("/roomtypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void testGetAllRoomTypes_VerifyFields() throws Exception {
        mockMvc.perform(get("/roomtypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // flexible match: take first array inside _embedded
                .andExpect(jsonPath("$._embedded.*[0].typeName").value(notNullValue()))
                .andExpect(jsonPath("$._embedded.*[0].description").value(notNullValue()))
                .andExpect(jsonPath("$._embedded.*[0].maxOccupancy").value(notNullValue()))
                .andExpect(jsonPath("$._embedded.*[0].pricePerNight").value(notNullValue()));
    }

    @Test
    void testGetAllRoomTypes_NoIdInResponse() throws Exception {
        mockMvc.perform(get("/roomtypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.*[0].roomTypeId").doesNotExist());
    }
}