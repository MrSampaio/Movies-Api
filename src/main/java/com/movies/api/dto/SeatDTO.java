package com.movies.api.dto;

public record SeatDTO(
        Long id,
        String rowLetter,
        Integer seatNumber,
        Boolean isAvailable
) {}