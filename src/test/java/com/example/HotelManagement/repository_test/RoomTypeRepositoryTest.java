package com.example.HotelManagement.repository_test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomTypeRepository;

@SpringBootTest
@Transactional
class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // TEST 1: FIND ALL RETURNS LIST
    @Test
    void testFindAllRoomTypes_ReturnsList() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        assertNotNull(roomTypes);
    }

    // TEST 2: FIND ALL RETURNS NON-NULL
    @Test
    void testFindAllRoomTypes_NotNull() {
        assertNotNull(roomTypeRepository.findAll());
    }

    // TEST 3: LIST CAN BE ITERATED
    @Test
    void testFindAllRoomTypes_Iterable() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType r : roomTypes) {
            assertNotNull(r);
        }
    }

    // TEST 4: METHOD EXECUTES WITHOUT ERROR
    @Test
    void testFindAllRoomTypes_Executes() {
        roomTypeRepository.findAll();
    }
}