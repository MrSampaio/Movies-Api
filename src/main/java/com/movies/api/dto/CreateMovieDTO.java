package com.movies.api.dto;

public record CreateMovieDTO(
        String title,
        String synopsis,
        String coverUrl
) {}