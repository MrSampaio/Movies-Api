package com.movies.api.repository;

import com.movies.api.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

    // SELECT * FROM showtime WHERE movie_id = ?
    List<Showtime> findByMovieId(Long movieId);
}