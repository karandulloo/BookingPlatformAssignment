package com.booking.platform.repository;

import com.booking.platform.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    Optional<Seat> findByScreenIdAndSeatNumber(Long screenId, String seatNumber);

    long countByScreenId(Long screenId);
}
