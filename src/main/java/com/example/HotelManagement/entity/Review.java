package com.example.HotelManagement.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer review_id;
private LocalDate review_date;
@Min(value = 0, message = "Rating cannot be less than 0")
@Max(value = 5, message = "Rating cannot be more than 5")
private int rating;
String comment;
@ManyToOne()
@JoinColumn(name="reservation_id")
private Reservation reservation;

}
