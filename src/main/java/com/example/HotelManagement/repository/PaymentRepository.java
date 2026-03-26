package com.example.HotelManagement.repository;

import com.example.HotelManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {

//    public List<Payment> findAll();

}
