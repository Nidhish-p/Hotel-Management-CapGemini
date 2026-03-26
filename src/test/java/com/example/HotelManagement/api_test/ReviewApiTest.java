package com.example.HotelManagement.api_test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewApiTest {
    @Autowired
    private MockMvc mockMvc;
    // TEST 1: SAVE REVIEW
    @Test
    void testSaveReview() throws Exception {

        String reviewJson = """
        {
            "review_date": "2026-03-25",
            "rating": 5,
            "comment": "Excellent stay!"
        }
        """;

        mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isCreated());
    }

    // TEST 2: GET ALL REVIEWS
    @Test
    void testGetAllReviews() throws Exception {

        mockMvc.perform(get("/review"))
                .andExpect(status().isOk());

    }
}
