package com.example.HotelManagement.repository_test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.repository.HotelRepository;

@SpringBootTest
public class HotelRepositoryTest {
    @Autowired
    private HotelRepository hotelRepository;

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
}
