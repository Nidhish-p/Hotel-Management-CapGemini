package com.example.HotelManagement.repository_test;


import com.example.HotelManagement.entity.Amenity;

import com.example.HotelManagement.repository.AmenityRepository;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;




import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class  AmenityRepositoryTest {

    @Autowired private AmenityRepository amenityRepository;
    @Autowired private RoomRepository    roomRepository;
    @Autowired private HotelRepository   hotelRepository;



    @Test
    @DisplayName("Amenity table has at least one record")
    void amenityTable_isNotEmpty() {
        assertThat(amenityRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Every amenity has a non-blank name")
    void everyAmenity_hasNonBlankName() {
        amenityRepository.findAll().forEach(a ->
                assertThat(a.getName()).isNotBlank()
        );
    }

    @Test
    @DisplayName("Every amenity has a non-blank description")
    void everyAmenity_hasNonBlankDescription() {
        amenityRepository.findAll().forEach(a ->
                assertThat(a.getDescription()).isNotBlank()
        );
    }

    @Test
    @DisplayName("Every amenity has a positive ID")
    void everyAmenity_hasPositiveId() {
        amenityRepository.findAll().forEach(a ->
                assertThat(a.getAmenityId()).isGreaterThan(0)
        );
    }



    @Test
    @DisplayName("findById returns correct amenity")
    void findById_returnsCorrectAmenity() {
        Amenity first = amenityRepository.findAll().get(0);

        Optional<Amenity> result = amenityRepository.findById(first.getAmenityId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(first.getName());
        assertThat(result.get().getAmenityId()).isEqualTo(first.getAmenityId());
    }

    @Test
    @DisplayName("findById with invalid ID returns empty")
    void findById_invalidId_returnsEmpty() {
        Optional<Amenity> result = amenityRepository.findById(999999);

        assertThat(result).isEmpty();
    }

    // ─── findByName ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByName returns amenity matching the first real amenity name")
    void findByName_returnsMatch() {
        String realName = amenityRepository.findAll().get(0).getName();

        List<Amenity> results = amenityRepository.findByName(realName);

        assertThat(results).isNotEmpty();
        results.forEach(a ->
                assertThat(a.getName()).isEqualTo(realName)
        );
    }

    @Test
    @DisplayName("findByName with non-existent name returns empty list")
    void findByName_noMatch_returnsEmpty() {
        List<Amenity> results = amenityRepository.findByName("DOES_NOT_EXIST_XYZ");

        assertThat(results).isEmpty();
    }





    @Test
    @DisplayName("Amenity.getRooms() is not null for every amenity")
    void everyAmenity_roomsCollectionIsNotNull() {
        amenityRepository.findAll().forEach(a -> {
            // trigger the lazy collection inside the open session
            assertThat(a.getRooms()).isNotNull();
        });
    }

    @Test
    @DisplayName("At least one amenity is linked to at least one room")
    void atLeastOneAmenity_isLinkedToARoom() {
        long amenitiesWithRooms = amenityRepository.findAll().stream()
                .filter(a -> !a.getRooms().isEmpty())
                .count();

        assertThat(amenitiesWithRooms).isGreaterThan(0);
    }

    @Test
    @DisplayName("Rooms linked to an amenity all have that amenity in their list")
    void amenityRooms_allContainThatAmenity() {
        // pick the first amenity that has at least one room linked
        Amenity amenity = amenityRepository.findAll().stream()
                .filter(a -> !a.getRooms().isEmpty())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No amenity with linked rooms found — check roomamenity table"));

        amenity.getRooms().forEach(room ->
                assertThat(room.getAmenities())
                        .extracting(Amenity::getAmenityId)
                        .contains(amenity.getAmenityId())
        );
    }

    @Test
    @DisplayName("Wi-Fi amenity exists and is linked to at least one room")
    void wifi_existsAndLinkedToRoom() {
        List<Amenity> wifiList = amenityRepository.findByName("Wi-Fi");

        assertThat(wifiList).isNotEmpty();
        Amenity wifi = wifiList.get(0);
        assertThat(wifi.getRooms()).isNotEmpty();
    }



    @Test
    @DisplayName("Amenity.getHotels() is not null for every amenity")
    void everyAmenity_hotelsCollectionIsNotNull() {
        amenityRepository.findAll().forEach(a ->
                assertThat(a.getHotels()).isNotNull()
        );
    }

    @Test
    @DisplayName("Hotels linked to an amenity all have that amenity in their list")
    void amenityHotels_allContainThatAmenity() {
        Amenity amenity = amenityRepository.findAll().stream()
                .filter(a -> !a.getHotels().isEmpty())
                .findFirst()
                .orElse(null);

        // only assert if the hotelamenity table has data
        if (amenity == null) return;

        amenity.getHotels().forEach(hotel ->
                assertThat(hotel.getAmenities())
                        .extracting(Amenity::getAmenityId)
                        .contains(amenity.getAmenityId())
        );
    }



    @Test
    @DisplayName("No two amenities share the same ID")
    void allAmenityIds_areUnique() {
        List<Integer> ids = amenityRepository.findAll().stream()
                .map(Amenity::getAmenityId)
                .toList();

        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("Total amenity count matches findAll size")
    void count_matchesFindAllSize() {
        long count      = amenityRepository.count();
        int  findAllSize = amenityRepository.findAll().size();

        assertThat(count).isEqualTo(findAllSize);
    }




}
