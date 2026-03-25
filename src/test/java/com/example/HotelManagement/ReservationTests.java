package com.example.HotelManagement;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.HotelManagement.entity.Reservation;
import com.example.HotelManagement.repository.ReservationRepository;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    // @DisplayName("Get All Reservations Test")
    void getAllReservationsTestRepo() {
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).isNotNull();
        assertThat(reservations).isNotEmpty(); // since data already exists
    }

    @Test
    void getAllReservationsTestApi() throws Exception {
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk());
    }
}