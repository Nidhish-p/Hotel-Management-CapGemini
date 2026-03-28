package com.example.HotelManagement.repository_test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.HotelManagement.entity.Amenity;
import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.entity.Review;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
public class HotelRepositoryTest {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // TEST 1: Find all hotels
    @Test
    void findAll_shouldReturnHotels() {
        assertThat(hotelRepository.findAll()).isNotNull();
    }

    // TEST 2: Save and find hotel
    @Test
    void saveAndFind_shouldPersistHotel() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("Test City");
        hotel.setDescription("Test Description");

        Hotel saved = hotelRepository.save(hotel);
        assertThat(hotelRepository.findById(saved.getHotelId())).isPresent();
    }

    // TEST 3: Update name for existing hotel via @Modifying query
    @Test
    void updateName_existingHotel_shouldUpdate() {
        Hotel hotel = hotelRepository.save(buildHotel("Old Name", "City", "Desc"));
        int id = hotel.getHotelId();

        int updated = hotelRepository.updateName(id, "New Name");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getName()).isEqualTo("New Name");
    }

    // TEST 4: Update name for non-existing hotel via @Modifying query
    @Test
    void updateName_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateName(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    // TEST 5: Update location for existing hotel via @Modifying query
    @Test
    void updateLocation_existingHotel_shouldUpdate() {
        Hotel hotel = hotelRepository.save(buildHotel("Hotel A", "Old Location", "Desc"));
        int id = hotel.getHotelId();

        int updated = hotelRepository.updateLocation(id, "New Location");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getLocation()).isEqualTo("New Location");
    }

    // TEST 6: Update location for invalid hotel via @Modifying query
    @Test
    void updateLocation_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateLocation(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    // TEST 7: Update description for existing hotel via @Modifying query
    @Test
    void updateDescription_existingHotel_shouldUpdate() {
        Hotel hotel = hotelRepository.save(buildHotel("Hotel B", "City", "Old Desc"));
        int id = hotel.getHotelId();

        int updated = hotelRepository.updateDescription(id, "New Desc");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getDescription()).isEqualTo("New Desc");
    }

    // TEST 8: Update description for invalid id via @Modifying query
    @Test
    void updateDescription_invalidHotel_shouldReturnZero() {
        int updated = hotelRepository.updateDescription(999999, "No Change");
        assertThat(updated).isEqualTo(0);
    }

    // TEST 9: Update description to null
    @Test
    void updateDescription_setToNull_shouldUpdate() {
        Hotel hotel = hotelRepository.save(buildHotel("Hotel Null Desc", "City", "Has Desc"));
        int id = hotel.getHotelId();

        int updated = hotelRepository.updateDescription(id, null);
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getDescription()).isNull();
    }

    // TEST 10: Fetch amenities for valid hotel name
    @Test
    void getAmenityByHotelName_existingHotelWithAmenities_shouldReturnAmenities() {
        Amenity wifi = buildAmenity("WiFi", "Wireless Internet");
        Amenity pool = buildAmenity("Pool", "Swimming Pool");
        entityManager.persist(wifi);
        entityManager.persist(pool);

        Hotel hotel = buildHotel("Amenity Hotel", "City", "Desc");
        hotel.setAmenities(Arrays.asList(wifi, pool));
        hotelRepository.save(hotel);
        entityManager.flush();

        List<Amenity> amenities = hotelRepository.getAmenityByHotelName("Amenity Hotel");
        assertThat(amenities).hasSize(2);
        assertThat(amenities)
                .extracting(a -> (Integer) ReflectionTestUtils.getField(a, "amenity_id"))
                .containsExactlyInAnyOrder(
                        (Integer) ReflectionTestUtils.getField(wifi, "amenity_id"),
                        (Integer) ReflectionTestUtils.getField(pool, "amenity_id"));
    }

    // TEST 11: Fetch amenities when none exist
    @Test
    void getAmenityByHotelName_existingHotelWithNoAmenities_shouldReturnEmpty() {
        Hotel hotel = buildHotel("Empty Amenities Hotel", "City", "Desc");
        hotel.setAmenities(Collections.emptyList());
        hotelRepository.save(hotel);
        entityManager.flush();

        List<Amenity> amenities = hotelRepository.getAmenityByHotelName("Empty Amenities Hotel");
        assertThat(amenities).isEmpty();
    }

    // TEST 12: Fetch amenities for non-existing hotel
    @Test
    void getAmenityByHotelName_missingHotel_shouldReturnEmpty() {
        List<Amenity> amenities = hotelRepository.getAmenityByHotelName("Missing Hotel");
        assertThat(amenities).isEmpty();
    }

    // TEST 13: Case insensitive name search
    @Test
    void getHotelsByName_caseInsensitive_shouldReturnMatches() {
        Hotel hotel = buildHotel("SunRise Inn", "City", "Desc");
        hotelRepository.save(hotel);

        List<Hotel> results = hotelRepository.findByNameIgnoreCaseContaining("sunrise inn");
        assertThat(results).extracting(Hotel::getName).contains("SunRise Inn");
    }

    // TEST 14: Partial match using LIKE query
    @Test
    void getHotelsByName_partialMatch_shouldReturnMatches() {
        Hotel hotel = buildHotel("Ocean View Resort", "City", "Desc");
        hotelRepository.save(hotel);

        List<Hotel> results = hotelRepository.findByNameIgnoreCaseContaining("View");
        assertThat(results).extracting(Hotel::getName).contains("Ocean View Resort");
    }

    // TEST 15: SQL injection attempt in name input
    @Test
    void getHotelsByName_sqlInjectionAttempt_shouldReturnEmpty() {
        Hotel hotel = buildHotel("Safe Hotel", "City", "Desc");
        hotelRepository.save(hotel);

        List<Hotel> results = hotelRepository.findByNameIgnoreCaseContaining("' OR 1=1 --");
        assertThat(results).isEmpty();
    }

    // TEST 16: Fetch large dataset efficiently
    @Test
    void getHotelsByLocation_largeDataset_shouldReturnAll() {
        String location = "MegaCity";
        for (int i = 0; i < 50; i++) {
            Hotel hotel = buildHotel("Bulk Hotel " + i, location, "Desc");
            hotelRepository.save(hotel);
        }

        List<Hotel> results = hotelRepository.findByLocation(location);
        assertThat(results).hasSize(50);
    }

    // TEST 17: Fetch sorted ascending
    // @Test
    // void getHotelsByName_sortedAscending_shouldReturnOrdered() {
    //     hotelRepository.save(buildHotel(nextHotelId(), "Alpha Inn", "City", "Desc"));
    //     hotelRepository.save(buildHotel(nextHotelId(), "Zulu Inn", "City", "Desc"));
    //     hotelRepository.save(buildHotel(nextHotelId(), "Bravo Inn", "City", "Desc"));

    //     List<Hotel> results = hotelRepository.findByNameIgnoreCaseContainingOrderByNameAsc("Inn");
    //     assertThat(results).extracting(Hotel::getName)
    //             .containsExactly("Alpha Inn", "Bravo Inn", "Zulu Inn");
    // }

    // TEST 18: Fetch sorted descending
    @Test
    void getHotelsByName_sortedDescending_shouldReturnOrdered() {
        String marker = "SuitesSortDesc";
        hotelRepository.save(buildHotel("Alpha " + marker, "City", "Desc"));
        hotelRepository.save(buildHotel("Zulu " + marker, "City", "Desc"));
        hotelRepository.save(buildHotel("Bravo " + marker, "City", "Desc"));

        List<Hotel> results = hotelRepository.findByNameIgnoreCaseContainingOrderByNameDesc(marker);
        assertThat(results).extracting(Hotel::getName)
                .containsExactly("Zulu " + marker, "Bravo " + marker, "Alpha " + marker);
    }

    // TEST 19: Update with same value (no change)
    @Test
    void updateName_sameValue_shouldUpdateWithoutChange() {
        Hotel hotel = hotelRepository.save(buildHotel("Same Name", "City", "Desc"));
        int id = hotel.getHotelId();

        int updated = hotelRepository.updateName(id, "Same Name");
        assertThat(updated).isEqualTo(1);

        Hotel refreshed = hotelRepository.findById(id).orElseThrow();
        assertThat(refreshed.getName()).isEqualTo("Same Name");
    }

    // TEST 20: Delete hotel with cascade (orphanRemoval=true)
    @Test
    void deleteById_withAmenitiesLinked_shouldDeleteHotel() {
        Amenity wifi = buildAmenity("WiFi", "Wireless Internet");
        Amenity pool = buildAmenity("Pool", "Swimming Pool");
        entityManager.persist(wifi);
        entityManager.persist(pool);

        Hotel hotel = buildHotel("Cascade Hotel", "City", "Desc");
        hotel.setAmenities(Arrays.asList(wifi, pool));
        Room room = new Room();
        room.setRoomNumber(101);
        RoomType roomType = createRoomType("Suite");
        room.setRoomTypeId(roomType.getRoomTypeId());
        room.setIsAvailable(true);
        room.setHotel(hotel);

        Reservation reservation = new Reservation();
        reservation.setGuestName("Test Guest");
        reservation.setGuestEmail("guest@example.com");
        reservation.setGuest_phone("9999999999");
        reservation.setRoom(room);

        Payment payment = new Payment();
        payment.setAmount(1000.0);
        payment.setPayment_status("PAID");
        payment.setReservation(reservation);

        Review review = new Review();
        review.setRating(5);
        review.setComment("Great stay");
        review.setReservation(reservation);

        reservation.setPayments(List.of(payment));
        reservation.setReviews(List.of(review));
        room.setReservation(List.of(reservation));
        hotel.setRooms(List.of(room));

        Hotel saved = hotelRepository.save(hotel);
        int id = saved.getHotelId();
        entityManager.flush();
        Integer roomId = room.getRoomId();
        Integer reservationId = reservation.getReservation_id();
        Integer paymentId = payment.getPayment_id();
        Integer reviewId = review.getReview_id();

        hotelRepository.deleteById(id);
        entityManager.flush();

        assertThat(hotelRepository.findById(id)).isEmpty();
        assertThat(entityManager.find(Room.class, roomId)).isNull();
        assertThat(entityManager.find(Reservation.class, reservationId)).isNull();
        assertThat(entityManager.find(Payment.class, paymentId)).isNull();
        assertThat(entityManager.find(Review.class, reviewId)).isNull();
        Integer wifiId = (Integer) ReflectionTestUtils.getField(wifi, "amenity_id");
        assertThat(entityManager.find(Amenity.class, wifiId)).isNotNull();
    }

    // TEST 21: Delete hotel linked with amenities - cascade/restrict check
    @Test
    void deleteById_withLinkedAmenities_shouldDeleteHotelOnly() {
        Amenity gym = buildAmenity("Gym", "Fitness Center");
        Amenity spa = buildAmenity("Spa", "Wellness");
        entityManager.persist(gym);
        entityManager.persist(spa);

        Hotel hotel = buildHotel("Linked Amenities Hotel", "City", "Desc");
        hotel.setAmenities(Arrays.asList(gym, spa));
        Hotel saved = hotelRepository.save(hotel);
        int id = saved.getHotelId();
        entityManager.flush();

        hotelRepository.deleteById(id);
        entityManager.flush();

        assertThat(hotelRepository.findById(id)).isEmpty();
        Integer gymId = (Integer) ReflectionTestUtils.getField(gym, "amenity_id");
        Integer spaId = (Integer) ReflectionTestUtils.getField(spa, "amenity_id");
        assertThat(entityManager.find(Amenity.class, gymId)).isNotNull();
        assertThat(entityManager.find(Amenity.class, spaId)).isNotNull();
    }

    // TEST 22: Fetch hotels by location with joined amenities
    @Test
    void getHotelsByLocation_withAmenities_shouldLoadAmenities() {
        Amenity wifi = buildAmenity("WiFi", "Wireless Internet");
        Amenity pool = buildAmenity("Pool", "Swimming Pool");
        entityManager.persist(wifi);
        entityManager.persist(pool);

        String location = "Amenity City";
        Hotel hotel = buildHotel("Amenity Join Hotel", location, "Desc");
        hotel.setAmenities(Arrays.asList(wifi, pool));
        hotelRepository.save(hotel);
        entityManager.flush();

        List<Hotel> results = hotelRepository.findByLocation(location);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAmenities()).hasSize(2);
    }

    // TEST 23: Insert with failure mid-transaction to verify rollback
    @Test
    void save_withFailureMidTransaction_shouldRollback() {
        final int[] idHolder = new int[1];
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        template.executeWithoutResult(status -> {
            Hotel hotel = buildHotel("Rollback Hotel", "City", "Desc");
            hotelRepository.save(hotel);
            idHolder[0] = hotel.getHotelId();
            status.setRollbackOnly();
        });

        assertThat(hotelRepository.findById(idHolder[0])).isEmpty();
    }

    // TEST 24: Paginate all hotels via JPA findAll(Pageable)
    @Test
    void findAll_withPageable_shouldReturnPage() {
        for (int i = 0; i < 15; i++) {
            hotelRepository.save(buildHotel("PageAll " + i, "City", "Desc"));
        }

        Page<Hotel> page = hotelRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(15);
    }

    // TEST 25: Paginate hotels filtered by location
    @Test
    void findByLocationIgnoreCase_withPageable_shouldReturnFilteredPage() {
        String location = "PagedCity";
        for (int i = 0; i < 8; i++) {
            hotelRepository.save(buildHotel("Loc " + i, location, "Desc"));
        }
        hotelRepository.save(buildHotel("Other Loc", "OtherCity", "Desc"));

        Page<Hotel> page = hotelRepository.findByLocationIgnoreCase(location, PageRequest.of(0, 5));
        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getContent()).extracting(Hotel::getLocation).allMatch(loc -> loc.equalsIgnoreCase(location));
    }

    // TEST 26: Paginate hotels with partial name match
    @Test
    void findByNameContainingIgnoreCase_withPageable_shouldReturnFilteredPage() {
        String marker = "PagedName";
        for (int i = 0; i < 7; i++) {
            hotelRepository.save(buildHotel(marker + " " + i, "City", "Desc"));
        }
        hotelRepository.save(buildHotel("Other Name", "City", "Desc"));

        Page<Hotel> page = hotelRepository.findByNameContainingIgnoreCase(marker, PageRequest.of(0, 4));
        assertThat(page.getContent()).hasSize(4);
        assertThat(page.getContent()).extracting(Hotel::getName).allMatch(name -> name.toLowerCase().contains(marker.toLowerCase()));
    }

    // TEST 27: Verify totalPages and totalElements in Page metadata
    @Test
    void findAll_withPageable_shouldHaveCorrectMetadata() {
        for (int i = 0; i < 23; i++) {
            hotelRepository.save(buildHotel("Meta " + i, "City", "Desc"));
        }

        Page<Hotel> page = hotelRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(23);
        assertThat(page.getTotalPages()).isGreaterThanOrEqualTo(3);
    }

    // TEST 28: Paginate with Sort - hotels ordered by name ascending
    @Test
    void findAll_withSortByNameAsc_shouldReturnOrdered() {
        String marker = "SortMeta";
        hotelRepository.save(buildHotel("Charlie " + marker, "City", "Desc"));
        hotelRepository.save(buildHotel("Alpha " + marker, "City", "Desc"));
        hotelRepository.save(buildHotel("Bravo " + marker, "City", "Desc"));

        Page<Hotel> page = hotelRepository.findAll(
                PageRequest.of(0, 10, Sort.by("name").ascending()));

        List<String> names = page.getContent().stream()
                .map(Hotel::getName)
                .filter(n -> n.contains(marker))
                .toList();
        assertThat(names).containsExactly("Alpha " + marker, "Bravo " + marker, "Charlie " + marker);
    }

    // TEST 29: Paginate with empty dataset returns empty page
    @Test
    void findAll_withEmptyDataset_shouldReturnEmptyPage() {
        hotelRepository.deleteAll();
        entityManager.flush();

        Page<Hotel> page = hotelRepository.findAll(PageRequest.of(0, 10));
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(0);
    }

    private Hotel buildHotel(String name, String location, String description) {
        Hotel hotel = new Hotel();
        hotel.setName(name);
        hotel.setLocation(location);
        hotel.setDescription(description);
        return hotel;
    }

    private Amenity buildAmenity(String name, String description) {
        Amenity amenity = new Amenity();
        ReflectionTestUtils.setField(amenity, "name", name);
        ReflectionTestUtils.setField(amenity, "description", description);
        return amenity;
    }

    private RoomType createRoomType(String name) {
        RoomType roomType = new RoomType();
        roomType.setTypeName(name + "-" + System.nanoTime());
        roomType.setDescription("Test type");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(java.math.BigDecimal.valueOf(999.99));
        return roomTypeRepository.save(roomType);
    }

}
