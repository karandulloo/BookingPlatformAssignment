package com.booking.platform.repository;

import com.booking.platform.model.entity.SeatBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatBookingRepository extends JpaRepository<SeatBooking, Long> {

    long countByShowId(Long showId);
}
