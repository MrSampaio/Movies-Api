package com.movies.api.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateMovieDTO(
        String title,
        String synopsis,
        String coverUrl,
        List<LocalDateTime> showtimes
) {}