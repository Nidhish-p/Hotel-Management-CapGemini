package com.example.HotelManagement.repository_test;


import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.AmenityRepository;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;



@SpringBootTest
@Transactional
class AmenityRepositoryTest {

    @Autowired private AmenityRepository  amenityRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private RoomTypeRepository roomTypeRepository;

    private Amenity wifi;
    private Amenity ac;
    private Amenity pool;
    private Amenity gym;
    private Room room1;
    private Room    room2;
    private Hotel hotelA;
    private Hotel   hotelB;

    @BeforeEach
    void setUp() {
        System.out.println("=== @BeforeEach — building amenity test data ===");

        wifi = saveAmenity("Free WiFi",       "High-speed internet");
        ac   = saveAmenity("Air Conditioning", "Central AC");
        pool = saveAmenity("Swimming Pool",    "Outdoor heated pool");
        gym  = saveAmenity("Gym",              "24-hour fitness centre");

        hotelA = hotelRepository.save(buildHotel("Grand Hyatt", "Mumbai"));
        hotelB = hotelRepository.save(buildHotel("Taj Palace",  "Delhi"));

        RoomType deluxe = new RoomType();
        deluxe.setTypeName("Deluxe");
        deluxe.setPricePerNight(BigDecimal.valueOf(4500.0));
        deluxe.setMaxOccupancy(2);
        deluxe = roomTypeRepository.save(deluxe);

        // room1 → wifi + ac  (hotelA)
        room1 = new Room();
        room1.setRoomNumber(101);
        room1.setHotel(hotelA);
        room1.setRoomType(deluxe);
        room1.setIsAvailable(true);
        room1.setAmenities(List.of(wifi, ac));
        room1 = roomRepository.save(room1);

        // room2 → pool + gym  (hotelB)
        room2 = new Room();
        room2.setRoomNumber(201);
        room2.setHotel(hotelB);
        room2.setRoomType(deluxe);
        room2.setIsAvailable(true);
        room2.setAmenities(List.of(pool, gym));
        room2 = roomRepository.save(room2);

        // hotelA → pool + gym  (hotel-level amenities)
        hotelA.setAmenities(List.of(pool, gym));
        hotelRepository.save(hotelA);

        // hotelB → wifi + ac
        hotelB.setAmenities(List.of(wifi, ac));
        hotelRepository.save(hotelB);


    }
    private Amenity saveAmenity(String name, String description) {
        Amenity a = new Amenity();
        a.setName(name);
        a.setDescription(description);
        return amenityRepository.save(a);
    }

    private Hotel buildHotel(String name, String location) {
        Hotel h = new Hotel();
        h.setName(name);
        h.setLocation(location);
        h.setDescription("Desc for " + name);
        return h;
    }

    private int indexOf(List<Amenity> list, Integer amenityId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAmenityId()==(amenityId)) return i;
        }
        return -1;
    }


    @AfterEach
    void tearDown() {
        System.out.println("=== @AfterEach — transaction rolled back, real DB untouched ===");
    }




}
