package com.example.HotelManagement.api_test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.HotelManagement.entity.Hotel;
import com.example.HotelManagement.entity.Room;
import com.example.HotelManagement.entity.RoomType;
import com.example.HotelManagement.repository.HotelRepository;
import com.example.HotelManagement.repository.RoomRepository;
import com.example.HotelManagement.repository.RoomTypeRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    // ================= HELPER =================

    private Room createRoom() {
        Hotel hotel = new Hotel();
        hotel.setName("Test Hotel");
        hotel.setLocation("City");
        hotel.setDescription("Desc");
        hotel = hotelRepository.save(hotel);

        RoomType roomType = new RoomType();
        roomType.setTypeName("Type-" + System.nanoTime());
        roomType.setDescription("Test type");
        roomType.setMaxOccupancy(2);
        roomType.setPricePerNight(java.math.BigDecimal.valueOf(999.99));
        roomType = roomTypeRepository.save(roomType);

        Room room = new Room();
        room.setRoomNumber(101);
        room.setRoomType(roomType);
        room.setIsAvailable(true);
        room.setHotel(hotel);

        return roomRepository.save(room);
    }

    // ================= INSERT =================

    @Test
    void createReservation_valid() throws Exception {

        Room room = createRoom();

        String body = """
        {
          "guestName": "Tanvi",
          "guestEmail": "tanvi@test.com",
          "guest_phone": "1234567890",
          "checkInDate": "2025-04-01",
          "checkOutDate": "2025-04-05",
          "room": { "roomId": %d }
        }
        """.formatted(room.getRoomId());

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void createReservation_invalid_dates() throws Exception {

        Room room = createRoom();

        String body = """
        {
          "guestName": "Tanvi",
          "guestEmail": "tanvi@test.com",
          "guest_phone": "1234567890",
          "checkInDate": "2025-04-10",
          "checkOutDate": "2025-04-05",
          "room": { "roomId": %d }
        }
        """.formatted(room.getRoomId());

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is4xxClientError());
    }

    // ================= DELETE =================

//     @Test
//     void deleteReservation_valid() throws Exception {

//         Room room = createRoom();

//         String body = """
//         {
//           "guestName": "Delete",
//           "guestEmail": "del@test.com",
//           "guest_phone": "1234567890",
//           "checkInDate": "2025-08-01",
//           "checkOutDate": "2025-08-03",
//           "room": { "roomId": %d }
//         }
//         """.formatted(room.getRoomId());

//         String response = mockMvc.perform(post("/api/reservations")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(body))
//                 .andReturn().getResponse().getContentAsString();

//         String id = response.split("\"id\":")[1].split(",")[0].trim();

//         mockMvc.perform(delete("/reservations/" + id))
//                 .andExpect(status().isNoContent());
//     }

    @Test
    void deleteReservation_invalid() throws Exception {
        mockMvc.perform(delete("/reservations/99999"))
                .andExpect(status().isNotFound());
    }

    // ================= GET =================

    @Test
    void getAllReservations() throws Exception {

            Room room = createRoom();

            mockMvc.perform(post("/api/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                            {
                                              "guestName": "API",
                                              "guestEmail": "api@test.com",
                                              "guest_phone": "123",
                                              "checkInDate": "2025-01-01",
                                              "checkOutDate": "2025-01-02",
                                              "room": { "roomId": %d }
                                            }
                                            """.formatted(room.getRoomId())));

            mockMvc.perform(get("/reservations"))
                            .andExpect(status().isOk());
    }

        @Test
        void getReservations_withPagination_existingData() throws Exception {

        mockMvc.perform(get("/reservations")
                .param("page", "0")
                .param("size", "5"))

                .andDo(print())
                        .andExpect(status().isOk());

                // ✅ minimal safe checks
                //.andExpect(jsonPath("$").exists());
        }


    @Test
    void getByGuestName() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "guestName": "Tanvi",
                  "guestEmail": "t@test.com",
                  "guest_phone": "123",
                  "checkInDate": "2025-01-01",
                  "checkOutDate": "2025-01-02",
                  "room": { "roomId": %d }
                }
                """.formatted(room.getRoomId())));

        mockMvc.perform(get("/reservations/search/findByGuestNameContainingIgnoreCase")
                .param("name", "tanvi"))
                .andExpect(status().isOk());
    }

    @Test
    void getByGuestEmail() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "guestName": "User",
                  "guestEmail": "tanvi@test.com",
                  "guest_phone": "123",
                  "checkInDate": "2025-01-01",
                  "checkOutDate": "2025-01-02",
                  "room": { "roomId": %d }
                }
                """.formatted(room.getRoomId())));

        mockMvc.perform(get("/reservations/search/findByGuestEmailContainingIgnoreCase")
                .param("email", "tanvi"))
                .andExpect(status().isOk());
    }

    @Test
    void getByCheckInDate() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "guestName": "CI",
                  "guestEmail": "ci@test.com",
                  "guest_phone": "123",
                  "checkInDate": "2024-11-01",
                  "checkOutDate": "2024-11-02",
                  "room": { "roomId": %d }
                }
                """.formatted(room.getRoomId())));

        mockMvc.perform(get("/reservations/search/findByCheckInDate")
                .param("date", "2024-11-01"))
                .andExpect(status().isOk());
    }

    @Test
    void getByCheckOutDate() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "guestName": "CO",
                  "guestEmail": "co@test.com",
                  "guest_phone": "123",
                  "checkInDate": "2025-03-20",
                  "checkOutDate": "2025-03-27",
                  "room": { "roomId": %d }
                }
                """.formatted(room.getRoomId())));

        mockMvc.perform(get("/reservations/search/findByCheckOutDate")
                .param("date", "2025-03-27"))
                .andExpect(status().isOk());
    }

    @Test
    void getCheckInBetween() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.format("""
            {
            "guestName": "Between",
            "guestEmail": "b@test.com",
            "guest_phone": "123",
            "checkInDate": "2024-10-10",
            "checkOutDate": "2024-10-12",
            "room": { "roomId": %d }
            }
            """, room.getRoomId())))
            .andExpect(status().isOk());

        mockMvc.perform(get("/reservations/search/findByCheckInDateBetween")
                .param("start", "2024-10-01")
                .param("end", "2024-10-20"))
                .andExpect(status().isOk());
    }

    @Test
    void getCheckOutBetween() throws Exception {

        Room room = createRoom();

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                {
                "guestName": "Between",
                "guestEmail": "b@test.com",
                "guest_phone": "123",
                "checkInDate": "2025-03-10",
                "checkOutDate": "2025-03-20",
                "room": { "roomId": %d }
                }
                """, room.getRoomId())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/reservations/search/findByCheckOutDateBetween")
                .param("start", "2025-01-01")
                .param("end", "2025-12-01"))
                .andExpect(status().isOk());
    }

    private Object jsonPath(String $pagesize) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
