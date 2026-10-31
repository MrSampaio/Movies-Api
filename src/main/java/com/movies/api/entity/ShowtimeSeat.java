package com.movies.api.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "showtime_seat")
public class ShowtimeSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relacionamento: muitos assentos pertencem a um horário
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @Column(name = "row_letter", nullable = false, length = 1)
    private String rowLetter;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}