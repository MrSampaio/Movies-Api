package com.movies.api.repository;

import com.movies.api.entity.ShowtimeSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, Long> {

    // SELECT * FROM showtime_seat WHERE showtime_id = ?
    List<ShowtimeSeat> findByShowtimeId(Long showtimeId);
}