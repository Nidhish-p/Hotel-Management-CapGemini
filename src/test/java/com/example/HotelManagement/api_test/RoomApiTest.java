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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoomApiTest {
    @Autowired private MockMvc mockMvc;

    @Autowired private RoomRepository roomRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private RoomTypeRepository roomTypeRepository;
    @Autowired private AmenityRepository amenityRepository;

    private Hotel hotelA;
    private Hotel    hotelB;
    private RoomType deluxe;
    private Amenity  wifi;
    private Amenity ac;
    private Amenity  pool;
    private Amenity  gym;
    private Room room1;
    private Room   room2;
    private Room     room3;
    private Room     room4;


    @BeforeEach
    void setUp() {
        System.out.println("=== @BeforeEach — building test data ===");

        hotelA = hotelRepository.save(buildHotel("Grand Hyatt", "Mumbai"));
        hotelB = hotelRepository.save(buildHotel("Taj Palace",  "Delhi"));

        deluxe = new RoomType();
        deluxe.setTypeName("Deluxe");
        deluxe.setPricePerNight(BigDecimal.valueOf(4500.0));
        deluxe.setMaxOccupancy(2);
        deluxe = roomTypeRepository.save(deluxe);

        wifi = saveAmenity("Free WiFi");
        ac   = saveAmenity("Air Conditioning");
        pool = saveAmenity("Swimming Pool");
        gym  = saveAmenity("Gym");

        room1 = saveRoom(101, hotelA, deluxe, true,  List.of(wifi, ac));
        room2 = saveRoom(102, hotelA, deluxe, false, List.of(pool, gym));
        room3 = saveRoom(201, hotelB, deluxe, true,  List.of(wifi, pool));
        room4 = saveRoom(202, hotelB, deluxe, true,  List.of());

        System.out.println("=== @BeforeEach — done ===");
    }


    @AfterEach
    void tearDown() {
        System.out.println("=== @AfterEach — transaction rolled back, DB untouched ===");
    }
    private Hotel buildHotel(String name, String location) {
        Hotel h = new Hotel();
        h.setName(name);
        h.setLocation(location);
        h.setDescription("Desc for " + name);
        return h;
    }

    private Room saveRoom(int number, Hotel hotel, RoomType rt,
                          boolean available, List<Amenity> amenities) {
        Room r = new Room();
        r.setRoomNumber(number);
        r.setHotel(hotel);
        r.setRoomType(rt);
        r.setIsAvailable(available);
        r.setAmenities(amenities);
        return roomRepository.save(r);
    }

    private Amenity saveAmenity(String name) {
        Amenity a = new Amenity();
        a.setName(name);
        a.setDescription("Desc for " + name);
        return amenityRepository.save(a);
    }


    @Test
    void getRoomAmenities_returns200() throws Exception {
        mockMvc.perform(get("/rooms/" + room1.getRoomId() + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test

    void getRoomAmenities_room1_returnsWifiAndAc() throws Exception {
        mockMvc.perform(get("/rooms/" + room1.getRoomId() + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(2)))
                .andExpect(jsonPath("$._embedded.amenities[*].name",
                        containsInAnyOrder("Free WiFi", "Air Conditioning")));
    }

    @Test
    @DisplayName("GET /rooms/{id}/amenities returns WiFi and Pool for room3")
    void getRoomAmenities_room3_returnsWifiAndPool() throws Exception {
        mockMvc.perform(get("/rooms/" + room3.getRoomId() + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(2)))
                .andExpect(jsonPath("$._embedded.amenities[*].name",
                        containsInAnyOrder("Free WiFi", "Swimming Pool")));
    }

    @Test

    void getRoomAmenities_room4_returnsEmpty() throws Exception {
        mockMvc.perform(get("/rooms/" + room4.getRoomId() + "/amenities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(0)));
    }
    @Test
    void fullNavigationFlow_hotelToRoomsToAmenities() throws Exception {

        // Page 1 — get hotelA
        mockMvc.perform(get("/hotels/" + hotelA.getHotelId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grand Hyatt"))
                .andExpect(jsonPath("$._links.rooms.href").exists());

        // Page 2 — get rooms for hotelA
        mockMvc.perform(get("/rooms/search/byHotel")
                        .param("hotelId", String.valueOf(hotelA.getHotelId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms", hasSize(2)))
                .andExpect(jsonPath("$._embedded.rooms[0]._links.amenities.href").exists());

        // Page 3 — get amenities for room1
        mockMvc.perform(get("/rooms/" + room1.getRoomId() + "/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities", hasSize(2)))
                .andExpect(jsonPath("$._embedded.amenities[*].name",
                        containsInAnyOrder("Free WiFi", "Air Conditioning")));
    }


}
