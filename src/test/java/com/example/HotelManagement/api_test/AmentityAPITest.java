package com.example.HotelManagement.api_test;


import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.AmenityRepository;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class AmentityAPITest {

    @Autowired private MockMvc           mockMvc;
    @Autowired private AmenityRepository amenityRepository;
    @Autowired private RoomRepository    roomRepository;
    @Autowired private HotelRepository   hotelRepository;

    // ─── LIST ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /amenities returns 200 and at least one amenity")
    void listAmenities_returns200() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities").isArray())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("GET /amenities response contains HAL links")
    void listAmenities_hasHalLinks() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._embedded.amenities[0]._links.self").exists());
    }

    @Test
    @DisplayName("GET /amenities each amenity has name and description")
    void listAmenities_eachHasNameAndDescription() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities[0].name").isString())
                .andExpect(jsonPath("$._embedded.amenities[0].description").isString());
    }

    @Test
    @DisplayName("GET /amenities real data — Wi-Fi exists in the list")
    void listAmenities_wifiExists() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities[*].name", hasItem("Wi-Fi")));
    }

    // ─── GET BY ID ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /amenities/{id} returns 200 with correct amenity")
    void getAmenity_byId_returns200() throws Exception {
        Integer id = amenityRepository.findAll().get(0).getAmenityId();

        mockMvc.perform(get("/amenities/" + id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.description").isString())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GET /amenities/999999 returns 404")
    void getAmenity_invalidId_returns404() throws Exception {
        mockMvc.perform(get("/amenities/999999"))
                .andExpect(status().isNotFound());
    }

    // ─── POST ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /amenities creates a new amenity and returns 201")
    @Transactional  // rolls back after test — real DB untouched
    void createAmenity_returns201() throws Exception {
        String body = """
                {
                  "name": "Rooftop Pool",
                  "description": "Heated rooftop pool with city views"
                }
                """;

        mockMvc.perform(post("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/amenities/")));
    }


    @Test
    @DisplayName("POST /amenities — response contains HAL self link")
    @Transactional
    void createAmenity_responseHasSelfLink() throws Exception {
        String body = """
                {
                  "name": "Spa",
                  "description": "Full service spa"
                }
                """;

        mockMvc.perform(post("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/amenities/")));
    }

    @Test
    @DisplayName("POST /amenities with empty name still accepted by SDR (no validation)")
    @Transactional
    void createAmenity_emptyName_stillCreated() throws Exception {

        String body = """
                {
                  "name": "",
                  "description": "No name amenity"
                }
                """;

        mockMvc.perform(post("/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }


    @Test
    @DisplayName("PATCH /amenities/{id} updates name only")
    @Transactional
    void patchAmenity_updatesNameOnly() throws Exception {
        int id = amenityRepository.findAll().get(0).getAmenityId();
        String originalDescription = amenityRepository.findAll().get(0).getDescription();

        mockMvc.perform(patch("/amenities/" + id)
                        .contentType(MediaType.parseMediaType("application/merge-patch+json"))
                        .content("""
                                { "name": "Updated Amenity Name" }
                                """))
                .andDo(print())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/amenities/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Amenity Name"))
                .andExpect(jsonPath("$.description").value(originalDescription));
    }



    @Test
    @DisplayName("GET /rooms/{id}/amenities returns amenities for a room")
    void getRoomAmenities_returns200() throws Exception {
        Integer roomId = roomRepository.findAll().get(0).getRoomId();

        mockMvc.perform(get("/rooms/" + roomId + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities").isArray());
    }

    @Test
    @DisplayName("GET /amenities/{id}/rooms returns rooms that have this amenity")
    void getAmenityRooms_returns200() throws Exception {
        Integer amenityId = amenityRepository.findAll().get(0).getAmenityId();

        mockMvc.perform(get("/amenities/" + amenityId + "/rooms"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /rooms/{id}/amenities adds an amenity to a room")
    @Transactional
    void addAmenityToRoom_returns204() throws Exception {
        Integer roomId    = roomRepository.findAll().get(0).getRoomId();
        Integer amenityId = amenityRepository.findAll().get(0).getAmenityId();
        String amenityUri = "http://localhost/amenities/" + amenityId;

        mockMvc.perform(put("/rooms/" + roomId + "/amenities")
                        .contentType(MediaType.parseMediaType("text/uri-list"))
                        .content(amenityUri))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /rooms/{id}/amenities/{amenityId} removes amenity from room")
    @Transactional
    void removeAmenityFromRoom_returns204() throws Exception {
        // pick a room that has at least one amenity
        var room = roomRepository.findAll().stream()
                .filter(r -> !r.getAmenities().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No room with amenities found in DB"));

        Integer roomId    = room.getRoomId();
        Integer amenityId = room.getAmenities().get(0).getAmenityId();

        mockMvc.perform(delete("/rooms/" + roomId + "/amenities/" + amenityId))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
