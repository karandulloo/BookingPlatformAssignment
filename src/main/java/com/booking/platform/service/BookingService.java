package com.booking.platform.service;

import com.booking.platform.dto.BookingRequestDto;
import com.booking.platform.dto.BookingResponseDto;
import com.booking.platform.exception.SeatUnavailableException;
import com.booking.platform.model.entity.Booking;
import com.booking.platform.model.entity.BookingStatus;
import com.booking.platform.model.entity.Seat;
import com.booking.platform.model.entity.SeatBooking;
import com.booking.platform.model.entity.Show;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.SeatBookingRepository;
import com.booking.platform.repository.SeatRepository;
import com.booking.platform.repository.ShowRepository;
import com.booking.platform.service.discount.DiscountEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final SeatBookingRepository seatBookingRepository;
    private final DiscountEngine discountEngine;

    @Transactional(rollbackFor = Exception.class)
    public BookingResponseDto createBooking(BookingRequestDto request) {
        var existing = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return toResponseDto(existing.get());
        }

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + request.getShowId()));

        Long screenId = show.getScreen().getId();
        List<Seat> seats = new ArrayList<>();
        for (String seatNumber : request.getSeats()) {
            Seat seat = seatRepository.findByScreenIdAndSeatNumber(screenId, seatNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatNumber + " for this show"));
            seats.add(seat);
        }

        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .show(show)
                .status(BookingStatus.INITIATED)
                .idempotencyKey(request.getIdempotencyKey())
                .build();
        booking = bookingRepository.save(booking);

        try {
            for (Seat seat : seats) {
                SeatBooking sb = SeatBooking.builder()
                        .show(show)
                        .seat(seat)
                        .booking(booking)
                        .build();
                seatBookingRepository.save(sb);
            }
        } catch (DataIntegrityViolationException e) {
            throw new SeatUnavailableException("One or more seats are already booked for this show", e);
        }

        BigDecimal totalAmount = discountEngine.apply(show, seats.size());

        mockPayment(booking.getId(), totalAmount);

        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return toResponseDto(booking);
    }

    private void mockPayment(Long bookingId, BigDecimal amount) {
        // placeholder for payment gateway
    }

    private static BookingResponseDto toResponseDto(Booking b) {
        return BookingResponseDto.builder()
                .id(b.getId())
                .userId(b.getUserId())
                .showId(b.getShow().getId())
                .status(b.getStatus())
                .totalAmount(b.getTotalAmount())
                .idempotencyKey(b.getIdempotencyKey())
                .build();
    }
}
