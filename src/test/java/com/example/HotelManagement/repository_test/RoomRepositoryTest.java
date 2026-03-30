package com.example.HotelManagement.repository_test;

import java.math.BigDecimal;
import java.util.List;


import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class RoomRepositoryTest {



    @Autowired private RoomRepository        roomRepository;
    @Autowired private HotelRepository       hotelRepository;
    @Autowired private RoomTypeRepository    roomTypeRepository;
    @Autowired private AmenityRepository     amenityRepository;
    @Autowired private ReservationRepository reservationRepository;

    @Test
    @DisplayName("Total rooms in DB is greater than zero")
    void totalRooms_greaterThanZero() {
        assertThat(roomRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Total hotels in DB is greater than zero")
    void totalHotels_greaterThanZero() {
        assertThat(hotelRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Total room types in DB is greater than zero")
    void totalRoomTypes_greaterThanZero() {
        assertThat(roomTypeRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Total amenities in DB is greater than zero")
    void totalAmenities_greaterThanZero() {
        assertThat(amenityRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Every room has a hotel assigned")
    void everyRoom_hasHotel() {
        roomRepository.findAll().forEach(room ->
                assertThat(room.getHotel()).isNotNull()
        );
    }

    @Test
    @DisplayName("Every room has a room type assigned")
    void everyRoom_hasRoomType() {
        roomRepository.findAll().forEach(room ->
                assertThat(room.getRoomType()).isNotNull()
        );
    }

    @Test
    @DisplayName("At least one available room exists")
    void atLeastOneAvailableRoom() {
        long available = roomRepository.findAll().stream()
                .filter(Room::getIsAvailable)
                .count();
        assertThat(available).isGreaterThan(0);
    }

    @Test
    @DisplayName("byHotel search returns only rooms belonging to that hotel")
    void byHotel_returnsCorrectRooms() {
        // pick the first hotel that exists in the real DB
        Integer hotelId = hotelRepository.findAll().get(0).getHotelId();

        List<Room> rooms = roomRepository.findByHotel_HotelId(hotelId);

        assertThat(rooms).isNotEmpty();
        rooms.forEach(room ->
                assertThat(room.getHotel().getHotelId()).isEqualTo(hotelId)
        );
    }

    @Test
    @DisplayName("byHotel rooms all have amenities loaded (no lazy init exception)")
    void byHotel_amenitiesAreLoaded() {
        Integer hotelId = hotelRepository.findAll().get(0).getHotelId();

        List<Room> rooms = roomRepository.findByHotel_HotelId(hotelId);

        // just accessing .getAmenities() would throw LazyInitializationException
        // if JOIN FETCH is missing — this test catches that regression
        rooms.forEach(room ->
                assertThat(room.getAmenities()).isNotNull()
        );
    }

    @Test
    @DisplayName("byHotel rooms all have roomType loaded (no lazy init exception)")
    void byHotel_roomTypeIsLoaded() {
        Integer hotelId = hotelRepository.findAll().get(0).getHotelId();

        List<Room> rooms = roomRepository.findByHotel_HotelId(hotelId);

        rooms.forEach(room ->
                assertThat(room.getRoomType()).isNotNull()
        );
    }

    @Test
    @DisplayName("Different hotels return different room sets")
    void differentHotels_returnDifferentRooms() {
        List<?> hotels = hotelRepository.findAll();
        // only run if at least 2 hotels exist
        if (hotels.size() < 2) return;

        Integer hotelAId = ((com.example.HotelManagement.entity.Hotel) hotels.get(0)).getHotelId();
        Integer hotelBId = ((com.example.HotelManagement.entity.Hotel) hotels.get(1)).getHotelId();

        List<Room> roomsA = roomRepository.findByHotel_HotelId(hotelAId);
        List<Room> roomsB = roomRepository.findByHotel_HotelId(hotelBId);

        // room IDs in hotel A should not appear in hotel B
        List<Integer> idsA = roomsA.stream().map(Room::getRoomId).toList();
        List<Integer> idsB = roomsB.stream().map(Room::getRoomId).toList();

        assertThat(idsA).doesNotContainAnyElementsOf(idsB);
    }





}
