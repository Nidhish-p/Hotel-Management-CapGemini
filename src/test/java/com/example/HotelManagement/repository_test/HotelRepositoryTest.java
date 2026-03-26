package com.example.HotelManagement.repository_test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.repository.HotelRepository;

@SpringBootTest
@Transactional
public class HotelRepositoryTest {
    @Autowired
    private HotelRepository hotelRepository;

    private static final AtomicInteger HOTEL_SEQ =
            new AtomicInteger((int) (System.currentTimeMillis() % 1_000_000) + 3_000_000);

    @Test
    void findAll_shouldReturnHotels() {
        assertThat(hotelRepository.findAll()).isNotNull();
    }

    @Test
    void saveAndFind_shouldPersistHotel() {
        Hotel hotel = new Hotel();
        hotel.setHotel_id(1);
        hotel.setName("Test Hotel");
        hotel.setLocation("Test City");
        hotel.setDescription("Test Description");

        hotelRepository.save(hotel);
        assertThat(hotelRepository.findById(1)).isPresent();
    }

    // TEST 6: Update name for existing hotel via @Modifying query
    @Test
    void updateName_existingHotel_shouldUpdate() {
        int id = nextHotelId();
        Hotel hotel = buildHotel(id, "Old Name", "City", "Desc");
        hotelRepository.save(hotel);

        int updated = hotelRepository.updateName(id, "New Name");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getName()).isEqualTo("New Name");
    }

    // TEST 7: Update name for non-existing hotel via @Modifying query
    @Test
    void updateName_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateName(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    // TEST 8: Update location for existing hotel via @Modifying query
    @Test
    void updateLocation_existingHotel_shouldUpdate() {
        int id = nextHotelId();
        Hotel hotel = buildHotel(id, "Hotel A", "Old Location", "Desc");
        hotelRepository.save(hotel);

        int updated = hotelRepository.updateLocation(id, "New Location");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getLocation()).isEqualTo("New Location");
    }

    // TEST 9: Update location for invalid hotel via @Modifying query
    @Test
    void updateLocation_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateLocation(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    // TEST 10: Update description for existing hotel via @Modifying query
    @Test
    void updateDescription_existingHotel_shouldUpdate() {
        int id = nextHotelId();
        Hotel hotel = buildHotel(id, "Hotel B", "City", "Old Desc");
        hotelRepository.save(hotel);

        int updated = hotelRepository.updateDescription(id, "New Desc");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getDescription()).isEqualTo("New Desc");
    }

    // TEST 11: Update description for invalid id via @Modifying query
    @Test
    void updateDescription_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateDescription(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    private int nextHotelId() {
        return HOTEL_SEQ.getAndIncrement();
    }

    private Hotel buildHotel(int id, String name, String location, String description) {
        Hotel hotel = new Hotel();
        hotel.setHotel_id(id);
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setDescription(description);
        return hotel;
    }

}
