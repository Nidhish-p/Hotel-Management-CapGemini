package com.example.HotelManagement.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer payment_id;

//    reservation_id
    private Double amount;
    private Date payment_date;
    private String payment_status;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Override
    public String toString() {
        return "Payment{" +
                "payment_id=" + payment_id +
                ", amount=" + amount +
                ", payment_date=" + payment_date +
                ", payment_status='" + payment_status + '\'' +
                '}';
    }
}
