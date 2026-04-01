package com.example.HotelManagement.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.dto.HotelDTO;
import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Hotel;

@Repository
@Transactional(readOnly = true)
@RepositoryRestResource(excerptProjection = HotelDTO.class)
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    @Transactional
    default int updateName(int id, String name) {
        Optional<Hotel> existing = findById(id);
        if (existing.isEmpty()) {
            return 0;
        }
        Hotel hotel = existing.get();
        hotel.setName(name);
        save(hotel);
        return 1;
    }

    @Transactional
    default int updateLocation(int id, String location) {
        Optional<Hotel> existing = findById(id);
        if (existing.isEmpty()) {
            return 0;
        }
        Hotel hotel = existing.get();
        hotel.setLocation(location);
        save(hotel);
        return 1;
    }

    @Transactional
    default int updateDescription(int id, String description) {
        Optional<Hotel> existing = findById(id);
        if (existing.isEmpty()) {
            return 0;
        }
        Hotel hotel = existing.get();
        hotel.setDescription(description);
        save(hotel);
        return 1;
    }

    List<Hotel> findByLocation(String location);

    List<Hotel> findByName(String name);

    List<Hotel> findByNameIgnoreCaseContaining(String name);

    List<Hotel> findByNameIgnoreCaseContainingOrderByNameAsc(String name);

    List<Hotel> findByNameIgnoreCaseContainingOrderByNameDesc(String name);

    Page<Hotel> findByLocationIgnoreCase(String location, Pageable pageable);

    Page<Hotel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndHotelIdNot(String name, Integer hotelId);

    @Transactional(readOnly = true)
    default List<Amenity> getAmenityByHotelName(String name) {
        List<Hotel> hotels = findByName(name);
        if (hotels.isEmpty()) {
            return List.of();
        }
        List<Amenity> amenities = new ArrayList<>();
        for (Hotel hotel : hotels) {
            List<Amenity> hotelAmenities = hotel.getAmenities();
            if (hotelAmenities != null && !hotelAmenities.isEmpty()) {
                amenities.addAll(hotelAmenities);
            }
        }
        return amenities;
    }

}
