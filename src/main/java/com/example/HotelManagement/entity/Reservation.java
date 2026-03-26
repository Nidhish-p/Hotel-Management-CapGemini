package com.example.HotelManagement.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    private Integer reservation_id;
    private String guest_name;
    private String guest_email;
    private String guest_phone;
    private LocalDate check_in_date;
    private LocalDate check_out_date;
}
