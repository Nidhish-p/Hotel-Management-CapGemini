package com.example.HotelManagement.repository_test;


import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.repository.AmenityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmenityRepositoryTest {

    @Autowired
    AmenityRepository amenityRepository;


    @Test
    void testAddAmenity(){
        Amenity amenity = new Amenity();
        amenity.setName("Wi-Fi");
        amenity.setDescription("High speed internet");

        Amenity savedAmenity = amenityRepository.save(amenity);

        assertNotNull(savedAmenity.getAmenity_id());
        assertEquals("Wi-Fi", savedAmenity.getName());
    }
    @Test
    void testFindAll() {
        List<Amenity> amenities = amenityRepository.findAll();
        assertNotNull(amenities);
        assertFalse(amenities.isEmpty());
    }
    @Test
    void testFindByName(){

        List<Amenity> amenities = amenityRepository.findByName("WI-FI");
        assertNotNull(amenities);
        assertFalse(amenities.isEmpty());
    }
    @Test
    void testFindById(){
        Amenity amenity = amenityRepository.findById(0).orElse(null);
        assertNotNull(amenity);
        assertEquals("Wi-Fi", amenity.getName());
    }
    @Test
    void testaddAmneity(){
        Amenity amenity = new Amenity();
        amenity.setAmenity_id(84);
        amenity.setName("BathRoom");
        amenity.setDescription("Bath Room with tube");
        amenityRepository.save(amenity);

        Amenity savedAmenity = amenityRepository.save(amenity);

        assertNotNull(savedAmenity.getAmenity_id());
        assertEquals("BathRoom", savedAmenity.getName());
        assertEquals("Bath Room with tube", savedAmenity.getDescription());

    }

    @Test
    void DeleteAmenityById(){
        Amenity amenity = amenityRepository.findById(0).orElse(null);
        assertNotNull(amenity);
        amenityRepository.delete(amenity);
    }

}
