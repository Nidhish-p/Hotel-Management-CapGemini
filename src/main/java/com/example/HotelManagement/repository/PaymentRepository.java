package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.PaymentDTO;
import com.example.HotelManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(
        collectionResourceRel = "payments",
        path = "payments",
        excerptProjection = PaymentDTO.class
)
public interface PaymentRepository extends JpaRepository<Payment,Integer> {

    @RestResource(path = "by-hotel", rel = "by-hotel")
    List<Payment> findByReservation_Room_Hotel_HotelId(@Param("hotelId") Integer hotelId);
}
