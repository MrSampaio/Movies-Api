package com.movies.api.dto;

public record MovieDTO(
        Long id,
        String title,
        String synopsis,
        String coverUrl
) {}