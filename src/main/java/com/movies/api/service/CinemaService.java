package com.movies.api.service;

import com.movies.api.dto.*;
import com.movies.api.entity.Movie;
import com.movies.api.entity.Showtime;
import com.movies.api.entity.ShowtimeSeat;
import com.movies.api.repository.MovieRepository;
import com.movies.api.repository.ShowtimeRepository;
import com.movies.api.repository.ShowtimeSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private ShowtimeSeatRepository showtimeSeatRepository;

    // busca os detalhes do filme pelo ID
    public MovieDetailsDTO getMovieDetails(long movieId) {

        // busca pelo id e atribui na variável. caso seja nulo, retorna status e erro de não encontrado
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found."));

        // cria a lista com os horários
        List<ShowtimeDTO> showtimes = showtimeRepository.findByMovieId(movieId)
                .stream()
                .map(s -> new ShowtimeDTO(s.getId(), s.getStartTime()))
                .toList();

        // cria uma instância do MovieDetailsDTO e já retorna com os dados do filme buscado
        return new MovieDetailsDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getSynopsis(),
                movie.getCoverUrl(),
                showtimes
        );
    }

    // cria uma lista com todos os assentos da sessão do filme respectivo
    public List<SeatDTO> getSeatsForShowtime(long showtimeId){

        // já instancia e retorna a lista a partir do repositório
        return showtimeSeatRepository.findByShowtimeId(showtimeId)
                .stream()
                .map(seat -> new SeatDTO(
                        seat.getId(),
                        seat.getRowLetter(),
                        seat.getSeatNumber(),
                        seat.getIsAvailable()
                ))
                .toList();
    }

    // método para criar filme, recebe o filme e converte
    public MovieDetailsDTO createMovie(CreateMovieDTO createDTO) {
        Movie newMovie = new Movie();
        newMovie.setTitle(createDTO.title());
        newMovie.setSynopsis(createDTO.synopsis());
        newMovie.setCoverUrl(createDTO.coverUrl());
        Movie savedMovie = movieRepository.save(newMovie);

        List<ShowtimeDTO> showtimeDTOs = new java.util.ArrayList<>();

        if (createDTO.showtimes() != null && !createDTO.showtimes().isEmpty()) {
            for (java.time.LocalDateTime startTime : createDTO.showtimes()) {
                Showtime newShowtime = new Showtime();
                newShowtime.setMovie(savedMovie); // Vincula ao filme recém-criado
                newShowtime.setStartTime(startTime);

                Showtime savedShowtime = showtimeRepository.save(newShowtime);

                showtimeDTOs.add(new ShowtimeDTO(savedShowtime.getId(), savedShowtime.getStartTime()));
            }
        }

        return new MovieDetailsDTO(
                savedMovie.getId(),
                savedMovie.getTitle(),
                savedMovie.getSynopsis(),
                savedMovie.getCoverUrl(),
                showtimeDTOs
        );
    }

    public SeatDTO createSeat(Long showtimeId, CreateSeatDTO createDTO) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horário não encontrado"));

        ShowtimeSeat newSeat = new ShowtimeSeat();
        newSeat.setShowtime(showtime);
        newSeat.setRowLetter(createDTO.rowLetter());
        newSeat.setSeatNumber(createDTO.seatNumber());
        newSeat.setIsAvailable(createDTO.isAvailable());

        ShowtimeSeat savedSeat = showtimeSeatRepository.save(newSeat);


        return new SeatDTO(
                savedSeat.getId(),
                savedSeat.getRowLetter(),
                savedSeat.getSeatNumber(),
                savedSeat.getIsAvailable()
        );
    }
}
