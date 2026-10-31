package com.movies.api.dto;

import java.util.List;

public record MovieDetailsDTO(
        Long id,
        String title,
        String synopsis,
        String coverUrl,
        List<ShowtimeDTO> showtimes
) {}