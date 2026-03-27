package com.example.HotelManagement.repository_test;


import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.repository.AmenityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmenityRepositoryTest {

    @Autowired
    AmenityRepository amenityRepository;


    Amenity amenity;

    // BEFORE EACH TEST
    @BeforeEach
    void setUp() {
        amenity = new Amenity();
        amenity.setName("Wi-Fi");
        amenity.setDescription("High speed internet");
        amenityRepository.save(amenity);
    }

    // AFTER EACH TEST
    @AfterEach
    void tearDown() {
        amenityRepository.deleteAll();
    }

    @Test
    void testAddAmenity(){
        Amenity newAmenity = new Amenity();
        newAmenity.setName("BathRoom");
        newAmenity.setDescription("Bath Room with tube");

        Amenity savedAmenity = amenityRepository.save(newAmenity);

        assertNotNull(savedAmenity.getAmenity_id());
        assertEquals("BathRoom", savedAmenity.getName());
    }

    @Test
    void testFindAll() {
        List<Amenity> amenities = amenityRepository.findAll();
        assertNotNull(amenities);
        assertFalse(amenities.isEmpty());
    }

    @Test
    void testFindByName(){
        List<Amenity> amenities = amenityRepository.findByName("Wi-Fi");
        assertNotNull(amenities);
        assertFalse(amenities.isEmpty());
    }

    @Test
    void testFindById(){
        Amenity foundAmenity = amenityRepository.findById(amenity.getAmenity_id()).orElse(null);
        assertNotNull(foundAmenity);
        assertEquals("Wi-Fi", foundAmenity.getName());
    }

    @Test
    void testDeleteAmenityById(){
        amenityRepository.deleteById(amenity.getAmenity_id());
        Amenity deletedAmenity = amenityRepository.findById(amenity.getAmenity_id()).orElse(null);
        assertNull(deletedAmenity);
    }

}
