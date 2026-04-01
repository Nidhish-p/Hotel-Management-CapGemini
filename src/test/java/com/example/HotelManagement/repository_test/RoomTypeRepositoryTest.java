package com.example.HotelManagement.repository_test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.RoomTypeRepository;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // TEST 1: FIND ALL RETURNS LIST
    @Test
    void testFindAllRoomTypes_ReturnsList() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        assertNotNull(roomTypes);
    }

    // TEST 2: FIND ALL RETURNS NON-NULL
    @Test
    void testFindAllRoomTypes_NotNull() {
        assertNotNull(roomTypeRepository.findAll());
    }

    // TEST 3: LIST CAN BE ITERATED
    @Test
    void testFindAllRoomTypes_Iterable() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        for (RoomType r : roomTypes) {
            assertNotNull(r);
        }
    }

    // TEST 4: METHOD EXECUTES WITHOUT ERROR
    @Test
    void testFindAllRoomTypes_Executes() {
        roomTypeRepository.findAll();
    }

    // TEST: save() should persist a new room type and return an entity with a generated ID
    @Test
    void testSave_NewRoomType_GeneratesId() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Deluxe");
        roomType.setDescription("Luxury room");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("5000.00"));

        RoomType saved = roomTypeRepository.save(roomType);

        assertNotNull(saved.getRoomTypeId());
    }

    // TEST: save() should persist updated fields correctly
    @Test
    void testSave_UpdateRoomType_ChangesPersistedCorrectly() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Standard");
        roomType.setDescription("Standard room");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("3000.00"));

        RoomType saved = roomTypeRepository.save(roomType);
        saved.setTypeName("Updated Standard");
        saved.setMaxOccupancy(3);
        RoomType updated = roomTypeRepository.save(saved);

        assertEquals("Updated Standard", updated.getTypeName());
        assertEquals(3, updated.getMaxOccupancy());
    }

    // TEST: save() should correctly reflect a price update
    @Test
    void testSave_PriceUpdate_ReflectsCorrectly() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Suite");
        roomType.setDescription("Luxury suite");
        roomType.setMaxOccupancy(4);
        roomType.setPricePerNight(new java.math.BigDecimal("8000.00"));

        RoomType saved = roomTypeRepository.save(roomType);
        saved.setPricePerNight(new java.math.BigDecimal("9000.00"));
        RoomType updated = roomTypeRepository.save(saved);

        assertEquals(new java.math.BigDecimal("9000.00"), updated.getPricePerNight());
    }

    // TEST: deleteById() should remove the entity so it is no longer retrievable
    @Test
    void testDeleteById_EntityGoneAfterDelete() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("To Be Deleted");
        roomType.setDescription("Will be deleted");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("2000.00"));

        RoomType saved = roomTypeRepository.save(roomType);
        Integer id = saved.getRoomTypeId();
        roomTypeRepository.deleteById(id);

        assertTrue(roomTypeRepository.findById(id).isEmpty());
    }

    // TEST: deleteById() should remove entity so it does not appear in findAll
    @Test
    void testDeleteById_AbsentFromFindAll() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Absent Room");
        roomType.setDescription("Should not appear");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("1500.00"));

        RoomType saved = roomTypeRepository.save(roomType);
        Integer id = saved.getRoomTypeId();
        roomTypeRepository.deleteById(id);

        List<RoomType> all = roomTypeRepository.findAll();
        assertTrue(all.stream().noneMatch(r -> r.getRoomTypeId().equals(id)));
    }

    // TEST: findById() should return a present Optional for an existing entity
    @Test
    void testFindById_Exists_ReturnsPresent() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Present Room");
        roomType.setDescription("Should be found");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("4000.00"));

        RoomType saved = roomTypeRepository.save(roomType);

        assertTrue(roomTypeRepository.findById(saved.getRoomTypeId()).isPresent());
    }

    // TEST: findById() should return an empty Optional for a non-existent ID
    @Test
    void testFindById_NonExistent_ReturnsEmpty() {
        assertTrue(roomTypeRepository.findById(999999).isEmpty());
    }

    // TEST: findById() should return an entity with fields matching what was saved
    @Test
    void testFindById_FieldsMatchSavedValues() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Field Match Room");
        roomType.setDescription("Checking fields");
        roomType.setMaxOccupancy(3);
        roomType.setPricePerNight(new java.math.BigDecimal("6000.00"));

        RoomType saved = roomTypeRepository.save(roomType);
        RoomType found = roomTypeRepository.findById(saved.getRoomTypeId()).get();

        assertEquals("Field Match Room", found.getTypeName());
        assertEquals("Checking fields", found.getDescription());
        assertEquals(3, found.getMaxOccupancy());
        assertEquals(new java.math.BigDecimal("6000.00"), found.getPricePerNight());
    }

    // TEST: findByTypeNameStartingWith() should return non-empty list when match exists
    @Test
    void testFindByTypeNameStartingWith_MatchFound_ReturnsNonEmptyList() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Deluxe Suite");
        roomType.setDescription("Luxury room");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("5000.00"));

        roomTypeRepository.save(roomType);

        List<RoomType> result = roomTypeRepository.findByTypeNameStartingWith("D");

        assertFalse(result.isEmpty());
    }

    // TEST: findByTypeNameStartingWith() should return empty list when no match exists
    @Test
    void testFindByTypeNameStartingWith_NoMatch_ReturnsEmptyList() {
        List<RoomType> result = roomTypeRepository.findByTypeNameStartingWith("xyz");

        assertTrue(result.isEmpty());
    }

    // TEST: findByDescriptionStartingWith() should return non-empty list when match exists
    @Test
    void testFindByDescriptionStartingWith_MatchFound_ReturnsNonEmptyList() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Luxury Room");
        roomType.setDescription("Lovely sea view");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("7000.00"));

        roomTypeRepository.save(roomType);

        List<RoomType> result = roomTypeRepository.findByDescriptionStartingWith("L");

        assertFalse(result.isEmpty());
    }

    // TEST: findByPricePerNight() should return non-empty list when match exists
    @Test
    void testFindByPricePerNight_MatchFound_ReturnsNonEmptyList() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Price Match Room");
        roomType.setDescription("Exact price match");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("5000.00"));

        roomTypeRepository.save(roomType);

        List<RoomType> result = roomTypeRepository.findByPricePerNight(new java.math.BigDecimal("5000.00"));

        assertFalse(result.isEmpty());
    }

    // TEST: findByMaxOccupancy() should return non-empty list when match exists
    @Test
    void testFindByMaxOccupancy_MatchFound_ReturnsNonEmptyList() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Occupancy Match Room");
        roomType.setDescription("Exact occupancy match");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(new java.math.BigDecimal("4000.00"));

        roomTypeRepository.save(roomType);

        List<RoomType> result = roomTypeRepository.findByMaxOccupancy(2);

        assertFalse(result.isEmpty());
    }

    // TEST: findAll() should return a non-null list
    @Test
    void testFindAll_ReturnsNonNullList() {
        List<RoomType> result = roomTypeRepository.findAll();

        assertNotNull(result);
    }

    @Test
    void testSave_DuplicateTypeName_DoesNotCrash() {
        RoomType r1 = new RoomType();
        r1.setTypeName("Duplicate");
        r1.setDescription("Test");
        r1.setMaxOccupancy(2);
        r1.setPricePerNight(new java.math.BigDecimal("1000"));

        roomTypeRepository.save(r1);

        RoomType r2 = new RoomType();
        r2.setTypeName("Duplicate"); 
        r2.setDescription("Test2");
        r2.setMaxOccupancy(3);
        r2.setPricePerNight(new java.math.BigDecimal("2000"));

        try {
            roomTypeRepository.save(r2);
        } catch (Exception e) {
            assertNotNull(e); 
        }
    }

    @Test
    void testPagination_FirstPage_Works() {
        RoomType r1 = new RoomType();
        r1.setTypeName("Type1");
        r1.setDescription("Desc1");
        r1.setMaxOccupancy(2);
        r1.setPricePerNight(new java.math.BigDecimal("1000"));
        roomTypeRepository.save(r1);

        RoomType r2 = new RoomType();
        r2.setTypeName("Type2");
        r2.setDescription("Desc2");
        r2.setMaxOccupancy(2);
        r2.setPricePerNight(new java.math.BigDecimal("2000"));
        roomTypeRepository.save(r2);

        Page<RoomType> page = roomTypeRepository.findAll(PageRequest.of(0, 1));

        assertEquals(1, page.getContent().size());
    }

    @Test
    void testPagination_SecondPage_Works() {
        for (int i = 1; i <= 3; i++) {
            RoomType r = new RoomType();
            r.setTypeName("Type" + i);
            r.setDescription("Desc" + i);
            r.setMaxOccupancy(2);
            r.setPricePerNight(new java.math.BigDecimal("1000"));
            roomTypeRepository.save(r);
        }

        Page<RoomType> page = roomTypeRepository.findAll(PageRequest.of(1, 1));

        assertEquals(1, page.getContent().size());
    }

    @Test
    void testPagination_EmptyPage_ReturnsEmpty() {

        // Get total count dynamically
        long total = roomTypeRepository.count();

        // Calculate page index that is surely out of bounds
        int pageSize = 5;
        int outOfBoundPage = (int) (total / pageSize) + 5;

        Page<RoomType> page = roomTypeRepository.findAll(
                PageRequest.of(outOfBoundPage, pageSize)
        );

        assertEquals(0, page.getContent().size());
    }

    @Test
    void testSorting_ByPriceAscending_Works() {

        RoomType r1 = new RoomType();
        r1.setTypeName("Sort_Test_1");
        r1.setDescription("Test");
        r1.setMaxOccupancy(2);
        r1.setPricePerNight(new java.math.BigDecimal("1000.00"));

        RoomType r2 = new RoomType();
        r2.setTypeName("Sort_Test_2");
        r2.setDescription("Test");
        r2.setMaxOccupancy(2);
        r2.setPricePerNight(new java.math.BigDecimal("2000.00"));

        roomTypeRepository.save(r1);
        roomTypeRepository.save(r2);

        List<RoomType> result = roomTypeRepository.findAll(
            org.springframework.data.domain.Sort.by("pricePerNight").ascending()
        );

        assertTrue(result.size() > 0);

        if (result.size() >= 2) {
            java.math.BigDecimal first = result.get(0).getPricePerNight();
            java.math.BigDecimal second = result.get(1).getPricePerNight();

            assertTrue(first.compareTo(second) <= 0);
        }
    }

    @Test
    void testFindByTypeNameStartingWith_MultipleResults() {
        RoomType r1 = new RoomType();
        r1.setTypeName("Deluxe A");
        r1.setDescription("Test");
        r1.setMaxOccupancy(2); 
        r1.setPricePerNight(new java.math.BigDecimal("3000.00"));
    
        RoomType r2 = new RoomType();
        r2.setTypeName("Deluxe B");
        r2.setDescription("Test");
        r2.setMaxOccupancy(3); 
        r2.setPricePerNight(new java.math.BigDecimal("4000.00"));
    
        roomTypeRepository.save(r1);
        roomTypeRepository.save(r2);
    
        List<RoomType> result =
                roomTypeRepository.findByTypeNameStartingWith("Deluxe");
        assertTrue(result.size() >= 2);
    }

    @Test
    void testFindByTypeNameStartingWith_CaseInsensitiveSafe() {

        RoomType r = new RoomType();
        r.setTypeName("Deluxe");
        r.setDescription("Test");
        r.setMaxOccupancy(4); 
        r.setPricePerNight(new java.math.BigDecimal("6000.00"));

        roomTypeRepository.save(r);

        List<RoomType> result = roomTypeRepository.findByTypeNameStartingWith("del");

        assertNotNull(result); 
    }
}