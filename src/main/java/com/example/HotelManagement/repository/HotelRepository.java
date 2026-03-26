package com.example.HotelManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.dto.HotelDTO;
import com.example.HotelManagement.entity.Hotel;

@Repository
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
}
