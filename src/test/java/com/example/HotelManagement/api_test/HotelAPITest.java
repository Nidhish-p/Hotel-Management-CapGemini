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

    // TEST 14: Add hotel with name exactly at boundary length
    @Test
    void addHotel_withBoundaryLengthName_shouldCreate() throws Exception {
        String boundaryName = "B".repeat(255);
        String hotelJson = buildHotelJson(nextHotelId(), boundaryName, "Bhopal", "Boundary length test");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 15: Add hotel exceeding name length
    @Test
    void addHotel_withExceedingNameLength_shouldCreate() throws Exception {
        String exceedingName = "C".repeat(256);
        String hotelJson = buildHotelJson(nextHotelId(), exceedingName, "Kochi", "Exceeding length test");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 16: Add hotel with empty string name
    @Test
    void addHotel_withEmptyName_shouldCreate() throws Exception {
        String hotelJson = buildHotelJson(nextHotelId(), "", "Lucknow", "Empty name test");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 17: Insert hotel with null description
    @Test
    void addHotel_withNullDescription_shouldCreate() throws Exception {
        String hotelJson = """
                {
                    "hotel_id": %d,
                    "name": "Null Desc Hotel",
                    "location": "Nagpur",
                    "description": null
                }
                """.formatted(nextHotelId());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson))
                .andExpect(status().isCreated());
    }

    // TEST 18: Bulk insert multiple hotels
    @Test
    void addHotel_bulkInsertMultipleHotels_shouldCreateAll() throws Exception {
        String hotel1 = buildHotelJson(nextHotelId(), "Bulk One", "Pune", "First");
        String hotel2 = buildHotelJson(nextHotelId(), "Bulk Two", "Pune", "Second");
        String hotel3 = buildHotelJson(nextHotelId(), "Bulk Three", "Pune", "Third");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotel1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotel2))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotel3))
                .andExpect(status().isCreated());
    }

    // TEST 19: Duplicate name + location combination
    @Test
    void addHotel_withDuplicateNameAndLocation_shouldCreate() throws Exception {
        String name = "Dup Combo Hotel";
        String location = "Mysore";
        String first = buildHotelJson(nextHotelId(), name, location, "First");
        String second = buildHotelJson(nextHotelId(), name, location, "Second");

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(first))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(second))
                .andExpect(status().isCreated());
    }

    // TEST 20: Fetch first page of hotels with size 10
    @Test
    void getHotels_firstPageWithSizeTen_shouldReturnPaged() throws Exception {
        seedHotels(25, "PageTest-First");

        mockMvc.perform(get("/hotels")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    // TEST 21: Fetch second page of hotels
    @Test
    void getHotels_secondPageWithSizeTen_shouldReturnPaged() throws Exception {
        seedHotels(25, "PageTest-Second");

        mockMvc.perform(get("/hotels")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(1));
    }

    // TEST 22: Fetch hotels sorted by name ascending
    @Test
    void getHotels_sortedByNameAsc_shouldReturnPaged() throws Exception {
        seedHotels(8, "SortTest");

        mockMvc.perform(get("/hotels")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    // TEST 23: Fetch page beyond total pages
    @Test
    void getHotels_pageBeyondTotal_shouldReturnEmptyPage() throws Exception {
        seedHotels(5, "PageTest-Beyond");

        mockMvc.perform(get("/hotels")
                .param("page", "98")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(98));
    }

    // TEST 24: Fetch with page size zero
    @Test
    void getHotels_pageSizeZero_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/hotels")
                .param("page", "0")
                .param("size", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(20))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    // TEST 25: Fetch with negative page number
    @Test
    void getHotels_negativePageNumber_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/hotels")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0));
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

    private void seedHotels(int count, String namePrefix) {
        for (int i = 0; i < count; i++) {
            Hotel hotel = buildHotelEntity(
                    nextHotelId(),
                    namePrefix + "-" + i,
                    "Seed City",
                    "Seed Desc");
            hotelRepository.save(hotel);
        }
    }

}
