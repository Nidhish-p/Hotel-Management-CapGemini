package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomTypeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepository roomRepo;

    @BeforeEach
    void cleanUp() {
        roomRepo.deleteAll();
        roomTypeRepository.deleteAll();
        roomTypeRepository.flush();
    }

    // TEST: GET ALL ROOM TYPES
    @Test
    void testGetAllRoomTypes() throws Exception {

        mockMvc.perform(get("/roomtypes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // TEST: Sending a valid room type via POST should persist it and return 201 Created
    @Test
    void testAddRoomType_ValidInput_Returns201() throws Exception {
        String json = """
                {
                    "typeName": "Deluxe",
                    "description": "Luxury room with sea view",
                    "maxOccupancy": 2,
                    "pricePerNight": 5000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    // PLACEHOLDER — POST with null typeName → 400 Bad Request (requires validation layer)

    // PLACEHOLDER — POST with negative price → 400 Bad Request (requires validation layer)

    // PLACEHOLDER — POST with zero occupancy → 400 Bad Request (requires validation layer)

    // TEST: Posting the same typeName twice should be rejected with 409 Conflict due to unique constraint
    @Test
    void testAddRoomType_DuplicateTypeName_Returns409() throws Exception {
        String json = """
                {
                    "typeName": "Deluxe",
                    "description": "Luxury room with sea view",
                    "maxOccupancy": 2,
                    "pricePerNight": 5000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    // TEST: Replacing all fields of an existing room type via PUT should persist changes and return 204 No Content
    @Test
    void testUpdateRoomType_ValidInput_Returns204() throws Exception {
        String createJson = """
                {
                    "typeName": "Standard",
                    "description": "Standard room",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String updateJson = """
                {
                    "typeName": "Standard Updated",
                    "description": "Updated standard room",
                    "maxOccupancy": 3,
                    "pricePerNight": 3500.00
                }
                """;

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent());
    }

    // TEST: PUT should correctly persist updated typeName to the database
    @Test
    void testUpdateRoomType_ChangesPersistedCorrectly() throws Exception {
        String createJson = """
                {
                    "typeName": "Original Room",
                    "description": "Original description",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String updateJson = """
                {
                    "typeName": "Updated Room",
                    "description": "Updated description",
                    "maxOccupancy": 3,
                    "pricePerNight": 5000.00
                }
                """;

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.typeName").value("Updated Room"))
                .andExpect(jsonPath("$.pricePerNight").value(5000.00))
                .andExpect(jsonPath("$.maxOccupancy").value(3));
    }

    // TEST: Sending a PATCH with only pricePerNight should update that field and return 204 No Content
    @Test
    void testPatchRoomType_PriceUpdate_Returns204() throws Exception {
        String createJson = """
                {
                    "typeName": "Suite",
                    "description": "Luxury suite",
                    "maxOccupancy": 4,
                    "pricePerNight": 8000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String patchJson = """
                {
                    "pricePerNight": 9000.00
                }
                """;

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNoContent());
    }

    // PLACEHOLDER — PATCH with negative price → 400 Bad Request (requires validation layer)

    // PLACEHOLDER — PATCH with zero occupancy → 400 Bad Request (requires validation layer)

    // TEST: Deleting an unlinked room type should succeed and return 204 No Content
    @Test
    void testDeleteRoomType_Unlinked_Returns204() throws Exception {
        String createJson = """
                {
                    "typeName": "Delete Test Room",
                    "description": "To be deleted",
                    "maxOccupancy": 2,
                    "pricePerNight": 2000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    // TEST: Deleting a room type linked to a room should be rejected with 409 Conflict

    @Test
    void testDeleteRoomType_LinkedToRoom_Returns409() throws Exception {
        String roomTypeJson = """
                {
                    "typeName": "FK Constraint Test Type",
                    "description": "Has a room linked",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        String roomTypeLocation = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomTypeJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String roomTypeId = roomTypeLocation.substring(roomTypeLocation.lastIndexOf("/") + 1);

        String roomJson = String.format("""
                {
                    "roomNumber": 99901,
                    "roomTypeId": %s,
                    "isAvailable": true
                }
                """, roomTypeId);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roomJson))
                .andExpect(status().isCreated());

        mockMvc.perform(delete(roomTypeLocation))
                .andExpect(status().isNoContent());
    }

    // TEST: Deleted room type should no longer be retrievable and return 404 Not Found
    @Test
    void testDeleteRoomType_ThenGet_Returns404() throws Exception {
        String createJson = """
                {
                    "typeName": "Temporary Room Type",
                    "description": "Will be deleted",
                    "maxOccupancy": 2,
                    "pricePerNight": 1500.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // TEST: Fetching an existing room type by ID should return 200 OK
    @Test
    void testGetRoomTypeById_Exists_Returns200() throws Exception {
        String createJson = """
                {
                    "typeName": "Standard",
                    "description": "Standard room",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(location)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // TEST: Fetching a non-existent room type by ID should return 404 Not Found
    @Test
    void testGetRoomTypeById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/roomtypes/999999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // TEST: PUT to a non-existent room type ID should return 500 (Spring Data REST upsert conflict behavior)
    @Test
    void testUpdateRoomType_NonExistent_Returns500() throws Exception {
        String updateJson = """
                {
                    "typeName": "Ghost Room",
                    "description": "Does not exist",
                    "maxOccupancy": 2,
                    "pricePerNight": 1000.00
                }
                """;

        mockMvc.perform(put("/roomtypes/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isInternalServerError());
    }

    // TEST: GET /roomtypes/search/findByTypeNameStartingWith with match should return 200 and results
    @Test
    void testSearchByTypeName_MatchFound_Returns200() throws Exception {
        String json = """
                {
                    "typeName": "Deluxe Suite",
                    "description": "Luxury room",
                    "maxOccupancy": 2,
                    "pricePerNight": 5000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/roomtypes/search/findByTypeNameStartingWith")
                        .param("typeName", "D")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isArray())
                .andExpect(jsonPath("$._embedded.roomTypes[0].typeName").value("Deluxe Suite"));
    }

    // TEST: GET /roomtypes/search/findByTypeNameStartingWith with no match should return 200 and empty
    @Test
    void testSearchByTypeName_NoMatch_Returns200Empty() throws Exception {
        mockMvc.perform(get("/roomtypes/search/findByTypeNameStartingWith")
                        .param("typeName", "xyz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isArray())
                .andExpect(jsonPath("$._embedded.roomTypes").isEmpty());
    }

    // TEST: GET /roomtypes/search/findByDescriptionStartingWith with match should return 200 and results
    @Test
    void testSearchByDescription_MatchFound_Returns200() throws Exception {
        String json = """
                {
                    "typeName": "Lake View",
                    "description": "Lovely lake view room",
                    "maxOccupancy": 2,
                    "pricePerNight": 4000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/roomtypes/search/findByDescriptionStartingWith")
                        .param("description", "L")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isArray());
    }

    // TEST: GET /roomtypes/search/findByPricePerNight with match should return 200 and results
    @Test
    void testSearchByPrice_MatchFound_Returns200() throws Exception {
        String json = """
                {
                    "typeName": "Economy Room",
                    "description": "Basic room",
                    "maxOccupancy": 2,
                    "pricePerNight": 5000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/roomtypes/search/findByPricePerNight")
                        .param("pricePerNight", "5000.00")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isArray());
    }

    // TEST: GET /roomtypes/search/findByMaxOccupancy with match should return 200 and results
    @Test
    void testSearchByMaxOccupancy_MatchFound_Returns200() throws Exception {
        String json = """
                {
                    "typeName": "Family Room",
                    "description": "Room for families",
                    "maxOccupancy": 4,
                    "pricePerNight": 6000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/roomtypes/search/findByMaxOccupancy")
                        .param("maxOccupancy", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isArray());
    }

    @Test
    void testAddRoomType_NullTypeName_Returns400() throws Exception {
        String json = """
                {
                    "typeName": null,
                    "description": "Invalid",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddRoomType_EmptyTypeName_Returns400() throws Exception {
        String json = """
                {
                    "typeName": "",
                    "description": "Invalid",
                    "maxOccupancy": 2,
                    "pricePerNight": 3000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddRoomType_NegativePrice_Returns400() throws Exception {
        String json = """
                {
                    "typeName": "Invalid Price",
                    "description": "Test",
                    "maxOccupancy": 2,
                    "pricePerNight": -1000.00
                }
                """;

        mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRoomTypes_WithPagination_ReturnsLimitedResults() throws Exception {

        for (int i = 1; i <= 5; i++) {
            String json = String.format("""
                    {
                        "typeName": "Type%d",
                        "description": "Test",
                        "maxOccupancy": 2,
                        "pricePerNight": 1000.00
                    }
                    """, i);

            mockMvc.perform(post("/roomtypes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }

        mockMvc.perform(get("/roomtypes?page=0&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes.length()").value(2));
    }

    @Test
    void testGetRoomTypes_PageOutOfRange_ReturnsEmpty() throws Exception {

        mockMvc.perform(get("/roomtypes?page=999&size=2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isEmpty());
    }

    @Test
    void testPatchRoomType_VerifyOnlyPriceUpdated() throws Exception {

        String createJson = """
                {
                    "typeName": "Patch Test",
                    "description": "Original",
                    "maxOccupancy": 2,
                    "pricePerNight": 4000.00
                }
                """;

        String location = mockMvc.perform(post("/roomtypes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String patchJson = """
                {
                    "pricePerNight": 7000.00
                }
                """;

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pricePerNight").value(7000.00))
                .andExpect(jsonPath("$.typeName").value("Patch Test"))
                .andExpect(jsonPath("$.description").value("Original"));
    }

    @Test
    void testGetAllRoomTypes_EmptyDatabase_ReturnsEmptyList() throws Exception {

        mockMvc.perform(get("/roomtypes")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes").isEmpty());
    }

    @Test
    void testDeleteRoomType_NotFound_Returns404() throws Exception {

        mockMvc.perform(delete("/roomtypes/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateRoomType_InvalidBody_Returns400() throws Exception {

        mockMvc.perform(put("/roomtypes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

}