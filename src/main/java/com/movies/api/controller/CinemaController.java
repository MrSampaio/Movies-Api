package com.movies.api.controller;

import com.movies.api.dto.MovieDetailsDTO;
import com.movies.api.dto.SeatDTO;
import com.movies.api.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cinema")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    /**
     * rota consumida pelo App Clip quando lê a Tag NFC
     * URL: GET http://localhost:8080/api/cinema/movies/1
     */
    @GetMapping("/movies/{id}")
    public ResponseEntity<MovieDetailsDTO> getMovie(@PathVariable Long id) {
        MovieDetailsDTO response = cinemaService.getMovieDetails(id);
        return ResponseEntity.ok(response);
    }

    /**
     * rota consumida pelo App Clip quando o usuário toca em um horário (para montar o LazyVGrid)
     * URL: GET http://localhost:8080/api/cinema/showtimes/1/seats
     */
    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<List<SeatDTO>> getSeats(@PathVariable Long id) {
        List<SeatDTO> response = cinemaService.getSeatsForShowtime(id);
        return ResponseEntity.ok(response);
    }
}