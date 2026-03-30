package com.example.HotelManagement.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@Entity
@Table(
        name = "hotel",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_hotel_name", columnNames = "name")
        }
)
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer hotelId;

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name must be at most 255 characters")
    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    @NotBlank(message = "location is required")
    @Size(max = 255, message = "location must be at most 255 characters")
    @Column(name = "location", length = 255, nullable = false)
    private String location;

    @NotBlank(message = "description is required")
    @Size(max = 255, message = "description must be at most 255 characters")
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "hotelamenity",
            joinColumns = @JoinColumn(
                    name = "hotel_id",
                    foreignKey = @ForeignKey(name = "fk_hotelamenity_hotel")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "amenityId",
                    foreignKey = @ForeignKey(name = "fk_hotelamenity_amenity")
            )
    )
    private List<Amenity> amenities;
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Room> rooms;
    
}
