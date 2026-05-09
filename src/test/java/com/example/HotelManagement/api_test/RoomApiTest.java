package com.example.HotelManagement.api_test;



import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;

import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoomApiTest {
    @Autowired private MockMvc           mockMvc;
    @Autowired private RoomRepository    roomRepository;
    @Autowired private HotelRepository   hotelRepository;



    private Hotel firstHotel() {
        return hotelRepository.findAll().get(0);
    }

    private Room firstRoom() {
        return roomRepository.findAll().get(0);
    }

    private Room firstRoomOfHotel(Integer hotelId) {
        return roomRepository.findByHotel_HotelId(hotelId).get(0);
    }



    @Test
    @DisplayName("GET /hotels returns 200 and at least one hotel")
    void getHotels_returns200() throws Exception {
        mockMvc.perform(get("/hotels"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels").isArray())
                .andExpect(jsonPath("$._embedded.hotels", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("GET /hotels/{id} returns correct hotel with HAL links")
    void getHotel_byId_returnsHotelWithLinks() throws Exception {
        Hotel hotel = firstHotel();

        mockMvc.perform(get("/hotels/" + hotel.getHotelId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(hotel.getName()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.rooms.href").exists());
    }

    @Test
    @DisplayName("GET /hotels/999999 returns 404")
    void getHotel_invalidId_returns404() throws Exception {
        mockMvc.perform(get("/hotels/999999"))
                .andExpect(status().isNotFound());
    }



    @Test
    @DisplayName("GET /rooms returns 200 and at least one room")
    void getRooms_returns200() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("GET /rooms returns rooms with roomType embedded")
    void getRooms_roomTypeIsEmbedded() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms[0].roomType").exists())
                .andExpect(jsonPath("$._embedded.rooms[0].roomType.typeName").isString());
    }

    @Test
    @DisplayName("GET /rooms returns rooms with amenities embedded")
    void getRooms_amenitiesAreEmbedded() throws Exception {
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms[0].amenities").isArray());
    }

    @Test
    @DisplayName("GET /rooms/{id} returns 200 with roomId present")
    void getRoom_byId_returns200() throws Exception {
        Room room = firstRoom();

        mockMvc.perform(get("/rooms/" + room.getRoomId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value(room.getRoomNumber()));
    }

    @Test
    @DisplayName("GET /rooms/999999 returns 404")
    void getRoom_invalidId_returns404() throws Exception {
        mockMvc.perform(get("/rooms/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /rooms/search/byHotel returns only rooms for that hotel")
    void byHotel_returnsRoomsForCorrectHotel() throws Exception {
        Hotel hotel = firstHotel();

        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$._embedded.rooms[*].roomId").exists());
    }

    @Test
    @DisplayName("GET /rooms/search/byHotel — every returned room belongs to that hotel")
    void byHotel_allRoomsBelongToHotel() throws Exception {
        Hotel hotel = firstHotel();


        var rooms = roomRepository.findByHotel_HotelId(hotel.getHotelId());
        rooms.forEach(r ->
                org.assertj.core.api.Assertions
                        .assertThat(r.getHotel().getHotelId())
                        .isEqualTo(hotel.getHotelId())
        );
    }

    @Test
    @DisplayName("GET /rooms/search/byHotel — rooms have amenities loaded")
    void byHotel_amenitiesAreLoaded() throws Exception {
        Hotel hotel = firstHotel();

        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms[0].amenities").isArray());
    }

    @Test
    @DisplayName("GET /rooms/search/byHotel — rooms have roomType loaded")
    void byHotel_roomTypeIsLoaded() throws Exception {
        Hotel hotel = firstHotel();

        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms[0].roomType").exists());
    }

    @Test
    @DisplayName("GET /rooms/search/byHotel?hotelId=999999 returns empty list")
    void byHotel_invalidHotelId_returnsEmpty() throws Exception {
        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms", hasSize(0)));
    }

    // ─── Amenities (Page 3) ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /rooms/{id}/amenities returns 200")
    void getRoomAmenities_returns200() throws Exception {
        Room room = firstRoom();

        mockMvc.perform(get("/rooms/" + room.getRoomId() + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /rooms/{id}/amenities returns amenities array")
    void getRoomAmenities_returnsArray() throws Exception {
        Room room = firstRoom();

        mockMvc.perform(get("/rooms/" + room.getRoomId() + "/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities").isArray());
    }

    @Test
    @DisplayName("GET /amenities returns 200 and at least one amenity")
    void getAmenities_returns200() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities").isArray())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(greaterThan(0))));
    }

    // ─── Full navigation flow ─────────────────────────────────────────────────

    @Test
    @DisplayName("Full flow: hotel → rooms → amenities — all 200, all links present")
    void fullNavigationFlow_hotelToRoomsToAmenities() throws Exception {
        Hotel hotel = firstHotel();

        // Page 1 — hotel exists and has rooms link
        mockMvc.perform(get("/hotels/" + hotel.getHotelId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(hotel.getName()))
                .andExpect(jsonPath("$._links.rooms.href").exists());

        // Page 2 — rooms for that hotel load with amenities embedded
        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", String.valueOf(hotel.getHotelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms").isArray())
                .andExpect(jsonPath("$._embedded.rooms[0].amenities").isArray())
                .andExpect(jsonPath("$._embedded.rooms[0].roomType").exists());

        // Page 3 — amenities endpoint for the first room of that hotel
        Room room = firstRoomOfHotel(hotel.getHotelId());
        mockMvc.perform(get("/rooms/" + room.getRoomId() + "/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities").isArray());
    }




}
