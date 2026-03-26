package com.example.HotelManagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Data
@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payment_id;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private Double amount;
    private Date payment_date;
    private String payment_status;

}
