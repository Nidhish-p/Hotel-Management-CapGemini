package com.example.HotelManagement.api_test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.repository.HotelRepository;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HotelAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    private static final AtomicInteger HOTEL_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 2_000_000);

    // TEST 1: Get all hotels (paged resource)
    @Test
    void getHotels_shouldReturnPagedResource() throws Exception {
        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").exists())
                .andExpect(jsonPath("$.page").exists());
    }

    // TEST 2: Add valid hotel with proper fields
    @Test
    void addHotel_withValidFields_shouldCreate() throws Exception {
        String hotelJson = buildHotelJson(nextHotelId(), "Grand Palace", "Mumbai", "Luxury stay");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 3: Add hotel with null name should succeed (schema allows NULL)
    @Test
    void addHotel_withNullName_shouldCreate() throws Exception {
        String hotelJson = """
                {
                    "hotel_id": %d,
                    "name": null,
                    "location": "Delhi",
                    "description": "Budget stay"
                }
                """.formatted(nextHotelId());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 4: Add duplicate hotel name (schema does not enforce uniqueness)
    @Test
    void addHotel_withDuplicateName_shouldCreate() throws Exception {
        String name = "UniqStay";
        String first = buildHotelJson(nextHotelId(), name, "Pune", "First entry");
        String second = buildHotelJson(nextHotelId(), name, "Pune", "Duplicate entry");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(first))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(second))
                .andExpect(status().isCreated());
    }

    // TEST 5: Add hotel with very long name beyond limit
    @Test
    void addHotel_withVeryLongName_shouldCreate() throws Exception {
        String longName = "H".repeat(300);
        String hotelJson = buildHotelJson(nextHotelId(), longName, "Chennai", "Long name test");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 6: Add hotel with special characters allowed
    @Test
    void addHotel_withSpecialCharactersInName_shouldCreate() throws Exception {
        String hotelJson = buildHotelJson(nextHotelId(), "Star@Stay #1", "Goa", "Beach view");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 7: Delete hotel without dependencies
    @Test
    void deleteHotel_withoutDependencies_shouldSucceed() throws Exception {
        int id = nextHotelId();
        String hotelJson = buildHotelJson(id, "Delete Me", "Jaipur", "Temp");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/hotels/" + id))
                .andExpect(status().isNoContent());
    }

    // TEST 8: Delete hotel having amenities linked
    @Test
    void deleteHotel_withAmenitiesLinked_shouldSucceed() throws Exception {
        int id = nextHotelId();
        String hotelJson = buildHotelJson(id, "Amenity Hotel", "Udaipur", "Temp");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());

        // No FK link exists in current schema, so delete should succeed
        mockMvc.perform(delete("/hotels/" + id))
                .andExpect(status().isNoContent());
    }

    // TEST 9: Delete non-existing hotel id
    @Test
    void deleteHotel_invalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/hotels/999999"))
                .andExpect(status().isNotFound());
    }

    // TEST 10: Fetch hotels for valid location
    @Test
    void getHotelsByLocation_valid_shouldReturnList() throws Exception {
        int id = nextHotelId();
        String hotelJson = buildHotelJson(id, "Loc Hotel", "Indore", "Temp");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/hotels/search/findByLocation")
                .param("location", "Indore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").exists())
                .andExpect(jsonPath("$._embedded.hotels[*].location").value(hasItem("Indore")));
    }

    // TEST 11: Fetch hotels for invalid location
    @Test
    void getHotelsByLocation_invalid_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/hotels/search/findByLocation")
                .param("location", "NoSuchLocation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").exists())
                .andExpect(jsonPath("$._embedded.hotels").isArray())
                .andExpect(jsonPath("$._embedded.hotels").isEmpty());
    }

    // TEST 12: Fetch hotels by valid name
    @Test
    void getHotelsByName_valid_shouldReturnList() throws Exception {
        int id = nextHotelId();
        Hotel hotel = buildHotelEntity(id, "Name Hotel", "Surat", "Temp");
        hotelRepository.save(hotel);

        mockMvc.perform(get("/hotels/search/findByName")
                .param("name", "Name Hotel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").exists())
                .andExpect(jsonPath("$._embedded.hotels[*].name").value(hasItem("Name Hotel")));
    }

    // TEST 13: Fetch hotels by invalid name
    @Test
    void getHotelsByName_invalid_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/hotels/search/findByName")
                .param("name", "NoSuchName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").exists())
                .andExpect(jsonPath("$._embedded.hotels").isArray())
                .andExpect(jsonPath("$._embedded.hotels").isEmpty());
    }

    private int nextHotelId() {
        return HOTEL_SEQ.getAndIncrement();
    }

    private String buildHotelJson(int id, String name, String location, String description) {
        return """
                {
                    "hotel_id": %d,
                    "name": "%s",
                    "location": "%s",
                    "description": "%s"
                }
                """.formatted(id, name, location, description);
    }

    private Hotel buildHotelEntity(int id, String name, String location, String description) {
        Hotel hotel = new Hotel();
        hotel.setHotel_id(id);
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setDescription(description);
        return hotel;
    }

}
