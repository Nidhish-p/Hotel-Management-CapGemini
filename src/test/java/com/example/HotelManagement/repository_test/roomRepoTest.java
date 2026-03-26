package com.example.HotelManagement.repository_test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.repository.RoomRepo;


@SpringBootTest
public class roomRepoTest {

    @Autowired
    private RoomRepo roomRepo;

    @Test
    void testFindAllRooms() {
        List<Room> rooms = roomRepo.findAll();
        assertNotNull(rooms);
        assertTrue(rooms.size() > 0);
    }

    @Test
    void testFindRoomById() {
        Room room = roomRepo.findById(1).orElse(null);
        assertNotNull(room);
        assertEquals(101, room.getRoomNumber());
    }

    @Test
    void testAddRoom() {
        Room room = new Room();
        room.setRoomId(100);
        room.setRoomNumber(999);
        room.setRoomTypeId(1);
        room.setIsAvailable(true);

        Room savedRoom = roomRepo.save(room);
        assertNotNull(savedRoom);
        assertEquals(999, savedRoom.getRoomNumber());
    }

    @Test
    void testUpdateRoom() {
        // Create a room first
        Room room = new Room();
        room.setRoomId(1);
        room.setRoomNumber(101);
        room.setRoomTypeId(1);
        
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
        room.setRoomId(200);
        room.setRoomNumber(888);
        room.setRoomTypeId(2);
        room.setIsAvailable(true);

        roomRepo.save(room);
        roomRepo.deleteById(200);

        assertFalse(roomRepo.findById(200).isPresent());
    }
}
