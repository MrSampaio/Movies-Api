package com.movies.api.dto;

import java.time.LocalDateTime;

public record ShowtimeDTO(
        Long id,
        LocalDateTime startTime
) {}
