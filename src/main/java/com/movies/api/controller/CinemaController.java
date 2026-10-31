package com.movies.api.controller;

import com.movies.api.dto.CreateMovieDTO;
import com.movies.api.dto.MovieDTO;
import com.movies.api.dto.MovieDetailsDTO;
import com.movies.api.dto.SeatDTO;
import com.movies.api.service.CinemaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
     * rota consumida pelo App Clip quando o usuário toca em um horário
     * URL: GET http://localhost:8080/api/cinema/showtimes/1/seats
     */
    @GetMapping("/showtimes/{id}/seats")
    public ResponseEntity<List<SeatDTO>> getSeats(@PathVariable Long id) {
        List<SeatDTO> response = cinemaService.getSeatsForShowtime(id);
        return ResponseEntity.ok(response);
    }

    /**
     * rota consumida para adicionar um novo filme no banco
     * URL: POST http://localhost:8080/api/cinema/movies
     * Body: JSON com title, synopsis e coverUrl
     */
    @PostMapping("/movies")
    public ResponseEntity<MovieDTO> createMovie(@RequestBody CreateMovieDTO dto) {
        MovieDTO createdMovie = cinemaService.createMovie(dto);

        // retorna status 201 Created com o corpo do filme criado
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

//    {
//        "title": "A Festa da Salsicha",
//        "synopsis": "Os alimentos de um supermercado acreditam que o momento em que são escolhidos pelos clientes é a ida para o paraíso. Porém, uma salsicha chamada Frank descobre a terrível e sangrenta verdade sobre o que os humanos realmente fazem com eles.",
//         "coverUrl": "https://media.themoviedb.org/t/p/w600_and_h900_face/l8XFkTzWxObRLFqYlgZ1UBgvw5r.jpg"
//    }
}