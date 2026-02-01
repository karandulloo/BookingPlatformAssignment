package com.booking.platform.repository;

import com.booking.platform.model.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieIdAndTheatre_City_IdAndShowDate(Long movieId, Long cityId, LocalDate showDate);
}
