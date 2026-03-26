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

    @Test
    void testSaveReviewWithInvalidReservationAPI() throws Exception {
        String reviewJson = """
    {
        "review_date": "2026-03-25",
        "rating": 4,
        "comment": "Invalid FK API test",
        "reservation": "http://localhost:8080/reservations/9999"
    }
    """;

        String location = mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isCreated())   // ✅ EXPECT SUCCESS
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Verify reservation is NULL
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservation").doesNotExist());
    }

    // TEST 2: GET ALL REVIEWS
    @Test
    void testGetAllReviews() throws Exception {

        mockMvc.perform(get("/review"))
                .andExpect(status().isOk());

    }
    @Test
    void testUpdateReview() throws Exception {

        String reviewJson = """
    {
        "review_date": "2026-03-25",
        "rating": 3,
        "comment": "Old comment"
    }
    """;

        // Creating review
        String response = mockMvc.perform(post("/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        // Extracting id from url
        String id = response.substring(response.lastIndexOf("/") + 1);

        // Update review
        String updatedJson = """
    {
        "review_date": "2026-03-25",
        "rating": 5,
        "comment": "Updated comment"
    }
    """;

        mockMvc.perform(put("/review/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNoContent());

        //Verify update
        mockMvc.perform(get("/review/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Updated comment"));
    }

}
