package com.example.HotelManagement.api_test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomTypeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomTypeApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomType savedRoomType;

    @BeforeEach
    void setup() {
        // Clean database
        roomTypeRepository.deleteAll();

        // Pre-populate sample data
        RoomType single = new RoomType("Single", "Single room description", 1, new BigDecimal("100.00"));
        RoomType doubleRoom = new RoomType("Double", "Double room description", 2, new BigDecimal("200.00"));

        // Save entities and capture the first one
        List<RoomType> savedRooms = roomTypeRepository.saveAll(List.of(single, doubleRoom));
        savedRoomType = savedRooms.get(0); // this entity has generated ID
    }

    @Test
    void testGetAllRoomTypes_Returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/roomtypes", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Check that names appear in the response
        assert(response.getBody().contains("Single"));
        assert(response.getBody().contains("Double"));
    }

    @Test
    void testGetAllRoomTypes_ReturnsList() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/roomtypes", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assert(response.getBody().contains("Single"));
        assert(response.getBody().contains("Double"));
    }

    @Test
    void testGetRoomTypeById_ReturnsCorrectRoom() {
        // Use saved entity ID in URL (guarantees correct entity)
        Integer id = savedRoomType.getRoomTypeId();

        ResponseEntity<RoomType> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/roomtypes/" + id, RoomType.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify fields instead of ID
        assertEquals(savedRoomType.getTypeName(), response.getBody().getTypeName());
        assertEquals(savedRoomType.getDescription(), response.getBody().getDescription());
        assertEquals(savedRoomType.getMaxOccupancy(), response.getBody().getMaxOccupancy());
        assertEquals(savedRoomType.getPricePerNight(), response.getBody().getPricePerNight());
    }
}