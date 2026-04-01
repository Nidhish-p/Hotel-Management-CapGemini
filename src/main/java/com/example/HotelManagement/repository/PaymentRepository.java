package com.example.HotelManagement.repository;

import com.example.HotelManagement.dto.PaymentDetailsDTO;
import com.example.HotelManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(
        collectionResourceRel = "payments",
        path = "payments",
        excerptProjection = PaymentDetailsDTO.class
)
public interface PaymentRepository extends JpaRepository<Payment,Integer>, PagingAndSortingRepository<Payment, Integer> {

    @RestResource(path = "by-hotel", rel = "by-hotel")
    List<Payment> findByReservation_Room_Hotel_HotelId(Integer hotelId);

    @RestResource(path = "by-payment-id", rel = "by-payment-id")
    PaymentDetailsDTO findByPaymentId(Integer paymentId);
}
