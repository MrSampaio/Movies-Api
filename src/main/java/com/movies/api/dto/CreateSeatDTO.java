package com.movies.api.dto;

public record CreateSeatDTO(
        String rowLetter,
        Integer seatNumber,
        Boolean isAvailable
) {}