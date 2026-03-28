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



    @Autowired private RoomRepository     roomRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private RoomTypeRepository roomTypeRepository;
    @Autowired private AmenityRepository amenityRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Autowired
    private jakarta.persistence.EntityManager em;

    private Hotel hotelA;
    private Hotel    hotelB;
    private RoomType deluxe;
    private Amenity  wifi;
    private Amenity  ac;
    private Amenity  pool;
    private Amenity gym;
    private Room     room1;
    private Room     room2;
    private Room     room3;
    private Room     room4;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        amenityRepository.deleteAllInBatch();
        roomTypeRepository.deleteAllInBatch();
        hotelRepository.deleteAllInBatch();

        System.out.println("=== @BeforeEach — setting up test data ===");

        // 2 Hotels
        hotelA = new Hotel();
        hotelA.setName("Grand Hyatt");
        hotelA.setLocation("Mumbai");
        hotelA = hotelRepository.save(hotelA);

        hotelB = new Hotel();
        hotelB.setName("Taj Palace");
        hotelB.setLocation("Delhi");
        hotelB = hotelRepository.save(hotelB);

        // 1 RoomType shared by all rooms
        deluxe = new RoomType();
        deluxe.setTypeName("Deluxe");
        deluxe.setPricePerNight(BigDecimal.valueOf(4500.0));
        deluxe.setMaxOccupancy(2);
        deluxe = roomTypeRepository.save(deluxe);

        // 4 Amenities
        wifi = saveAmenity("Free WiFi");
        ac   = saveAmenity("Air Conditioning");
        pool = saveAmenity("Swimming Pool");
        gym  = saveAmenity("Gym");

        // 4 Rooms — 2 per hotel, each with different amenities
        // room1 (hotelA) → Wi-Fi + AC
        room1 = new Room();
        room1.setRoomNumber(101);
        room1.setIsAvailable(true);
        room1.setHotel(hotelA);
        room1.setRoomType(deluxe);
        room1.setAmenities(List.of(wifi, ac));
        room1 = roomRepository.save(room1);

        // room2 (hotelA) → Pool + Gym
        room2 = new Room();
        room2.setRoomNumber(102);
        room2.setIsAvailable(false);
        room2.setHotel(hotelA);
        room2.setRoomType(deluxe);
        room2.setAmenities(List.of(pool, gym));
        room2 = roomRepository.save(room2);

        // room3 (hotelB) → Wi-Fi + Pool
        room3 = new Room();
        room3.setRoomNumber(201);
        room3.setIsAvailable(true);
        room3.setHotel(hotelB);
        room3.setRoomType(deluxe);
        room3.setAmenities(List.of(wifi, pool));
        room3 = roomRepository.save(room3);

        // room4 (hotelB) → AC + Gym
        room4 = new Room();
        room4.setRoomNumber(202);
        room4.setIsAvailable(true);
        room4.setHotel(hotelB);
        room4.setRoomType(deluxe);
        room4.setAmenities(List.of(ac, gym));
        room4 = roomRepository.save(room4);

        System.out.println("=== @BeforeEach — done. 2 hotels, 4 rooms, 4 amenities saved ===");
    }


    @AfterEach
    void tearDown() {
        System.out.println("=== @AfterEach — cleaning up ===");
        reviewRepository.deleteAllInBatch();
        paymentRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        roomRepository.deleteAllInBatch();
        amenityRepository.deleteAllInBatch();
        roomTypeRepository.deleteAllInBatch();
        hotelRepository.deleteAllInBatch();
        System.out.println("=== @AfterEach — done. All records deleted ===");
    }
    private Amenity saveAmenity(String name) {
        Amenity a = new Amenity();
        a.setName(name);
        a.setDescription("Desc for " + name);
        return amenityRepository.save(a);
    }

    @Test
    void testTotalRooms() {
        assertThat(roomRepository.count()).isEqualTo(4);
    }

    @Test
    void tsetTotalHotels(){
        assertThat(hotelRepository.count()).isEqualTo(2);
    }
    @Test
     void testTotalRoomType(){
        assertThat(roomTypeRepository.count()).isEqualTo(1);
     }
     @Test
     void testTotalAmenities(){
        assertThat(amenityRepository.count()).isEqualTo(4);
     }
     @Test
    void testRoom1(){
        Room room = roomRepository.findById(room1.getRoomId()).orElse(null);
        assertThat(room.getAmenities()).hasSize(2).extracting(Amenity::getName).containsExactly("Free WiFi", "Air Conditioning");
    }

    @Test
    void testRoom2(){
        Room room = roomRepository.findById(room2.getRoomId()).orElse(null);
        assertThat(room.getAmenities()).hasSize(2).extracting(Amenity::getName).containsExactly("Swimming Pool", "Gym");
    }

    @Test
    void testRoom3(){
        Room room = roomRepository.findById(room3.getRoomId()).orElse(null);
        assertThat(room.getAmenities()).hasSize(2).extracting(Amenity::getName).containsExactlyInAnyOrder("Swimming Pool", "Free WiFi");
    }

    @Test
    void testRoom4(){
        Room room = roomRepository.findById(room4.getRoomId()).orElseThrow();

        assertThat(room.getAmenities()).hasSize(2).extracting(Amenity::getName).containsExactly("Air Conditioning", "Gym");
    }

    @Test

    void testWifiRoom(){
        em.flush();
        em.clear();
        Amenity amenity = amenityRepository.findById(wifi.getAmenityId()).orElseThrow();
        amenity.getRooms().size();
        assertThat(amenity.getRooms()).hasSize(2).extracting(Room::getRoomNumber).containsExactlyInAnyOrder(201,101);
    }

    @Test
    void wifi_isSharedByRoom1AndRoom3() {
        Room loadedRoom1 = roomRepository.findById(room1.getRoomId()).orElseThrow();
        Room loadedRoom3 = roomRepository.findById(room3.getRoomId()).orElseThrow();

        assertThat(loadedRoom1.getAmenities())
                .extracting(Amenity::getAmenityId)
                .contains(wifi.getAmenityId());

        assertThat(loadedRoom3.getAmenities())
                .extracting(Amenity::getAmenityId)
                .contains(wifi.getAmenityId());
    }

    @Test
    void availableRooms_acrossBothHotels() {
        long available = roomRepository.findAll().stream()
                .filter(Room::getIsAvailable)
                .count();

        assertThat(available).isEqualTo(3);
    }





}
