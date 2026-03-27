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

import com.example.HotelManagement.repository.RoomRepo;
import com.example.HotelManagement.repository.RoomTypeRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomTypeApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private RoomRepo roomRepo;

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
                .andExpect(status().isConflict());
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
}