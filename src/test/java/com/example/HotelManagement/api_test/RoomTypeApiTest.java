package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomTypeApiTest {

    @Autowired
    private MockMvc mockMvc;

    // TEST: GET ALL ROOM TYPES
    @Test
    void testGetAllRoomTypes() throws Exception {

        mockMvc.perform(get("/roomtypes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}