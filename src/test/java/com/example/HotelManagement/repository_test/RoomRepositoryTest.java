package com.example.HotelManagement.repository_test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepo;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    void testFindAllRooms() {
        Room room = new Room();
        room.setRoomNumber(999);
        RoomType roomType = createRoomType("Basic");
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);
        roomRepo.save(room);

        List<Room> rooms = roomRepo.findAll();
        assertNotNull(rooms);
        assertTrue(rooms.size() > 0);
    }

    @Test
    void testFindRoomById() {
        Room room = new Room();
        room.setRoomNumber(101);
        RoomType roomType = createRoomType("Standard");
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);

        Room savedRoom = roomRepo.save(room);
        Room found = roomRepo.findById(savedRoom.getRoomId()).orElse(null);
        assertNotNull(found);
        assertEquals(101, found.getRoomNumber());
    }

    @Test
    void testAddRoom() {
        Room room = new Room();
        room.setRoomNumber(999);
        RoomType roomType = createRoomType("Deluxe");
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);

        Room savedRoom = roomRepo.save(room);
        assertNotNull(savedRoom);
        assertEquals(999, savedRoom.getRoomNumber());
    }

    @Test
    void testUpdateRoom() {
        // Create a room first
        Room room = new Room();
        room.setRoomNumber(101);
        RoomType roomType = createRoomType("Suite");
        room.setRoomTypeId(roomType.getRoomTypeId());
        
        room.setIsAvailable(true);

        Room savedRoom = roomRepo.save(room);

        // Update the room
        savedRoom.setIsAvailable(false);
        Room updatedRoom = roomRepo.save(savedRoom);

        // Verify update
        assertNotNull(updatedRoom);
        assertFalse(updatedRoom.getIsAvailable());
    }

    @Test
    void testDeleteRoom() {
        Room room = new Room();
        room.setRoomNumber(888);
        RoomType roomType = createRoomType("Economy");
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);

        Room savedRoom = roomRepo.save(room);
        roomRepo.deleteById(savedRoom.getRoomId());

        assertFalse(roomRepo.findById(savedRoom.getRoomId()).isPresent());
    }

    private RoomType createRoomType(String name) {
        RoomType roomType = new RoomType();
        roomType.setTypeName(name + "-" + System.nanoTime());
        roomType.setDescription("Test type");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(java.math.BigDecimal.valueOf(999.99));
        return roomTypeRepository.save(roomType);
    }
}
