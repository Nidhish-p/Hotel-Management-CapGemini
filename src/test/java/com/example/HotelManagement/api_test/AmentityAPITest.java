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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class AmentityAPITest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AmenityRepository amenityRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    HotelRepository hotelRepository;
    @Autowired
    RoomTypeRepository roomTypeRepository;

    private Amenity wifi;
    private Amenity pool;
    private Amenity gym;
    private Room room101;

    @BeforeEach
    void setup() {
        wifi = new Amenity();
        wifi.setName("WiFi");
        wifi.setDescription("High speed internet");
        amenityRepository.save(wifi);

        pool = new Amenity();
        pool.setName("Pool");
        pool.setDescription("Outdoor swimming pool");
        amenityRepository.save(pool);

        gym = new Amenity();
        gym.setName("Gym");
        gym.setDescription("Fully equipped gym");
        amenityRepository.save(gym);

        // one room so we can test room-amenity association endpoints
        RoomType deluxe = new RoomType(null, "Deluxe", "Deluxe room", 2,
                new BigDecimal("4500.00"));
        roomTypeRepository.save(deluxe);

        Hotel hotel = new Hotel();
        hotel.setName("Grand Hyatt");
        hotel.setLocation("Mumbai");
        hotel.setDescription("Luxury hotel");
        hotel.setAmenities(new ArrayList<>());
        hotelRepository.save(hotel);

        room101 = new Room();
        room101.setRoomNumber(101);
        room101.setIsAvailable(true);
        room101.setHotel(hotel);
        room101.setRoomType(deluxe);
        room101.setAmenities(new ArrayList<>(List.of(wifi, pool)));
        roomRepository.save(room101);
    }

    @AfterEach
    void teardown() {}

    // ─── LIST ────────────────────────────────────────────────────────────────

    @Test
    void listAmenities_returns200AndAllAmenities() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$._embedded.amenities[*].name",
                        hasItems("Wi-Fi", "Pool", "Gym")));
    }

    @Test
    void listAmenities_responseContainsHalLinks() throws Exception {
        mockMvc.perform(get("/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._embedded.amenities[0]._links.self").exists());
    }
}
