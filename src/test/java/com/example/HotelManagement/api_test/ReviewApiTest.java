package com.example.HotelManagement.api_test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ReviewApiTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
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
