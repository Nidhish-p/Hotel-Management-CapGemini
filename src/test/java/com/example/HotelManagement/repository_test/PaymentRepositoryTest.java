package com.example.HotelManagement.repository_test;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.HotelManagement.entity.Payment;
import com.example.HotelManagement.repository.PaymentRepository;


@SpringBootTest
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testGetAllPayment(){
        List<Payment> list = paymentRepository.findAll();
        System.out.println(list);

    }

    @Test
    public void testDeleteById(){
        paymentRepository.deleteById(2);

    }


}
