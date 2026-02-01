package com.booking.platform.service;

import com.booking.platform.dto.BookingRequestDto;
import com.booking.platform.dto.BookingResponseDto;
import com.booking.platform.exception.SeatUnavailableException;
import com.booking.platform.model.entity.Booking;
import com.booking.platform.model.entity.BookingStatus;
import com.booking.platform.model.entity.Screen;
import com.booking.platform.model.entity.Seat;
import com.booking.platform.model.entity.Show;
import com.booking.platform.repository.BookingRepository;
import com.booking.platform.repository.SeatBookingRepository;
import com.booking.platform.repository.SeatRepository;
import com.booking.platform.repository.ShowRepository;
import com.booking.platform.service.discount.DiscountEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ShowRepository showRepository;
    @Mock
    private SeatRepository seatRepository;
    @Mock
    private SeatBookingRepository seatBookingRepository;
    @Mock
    private DiscountEngine discountEngine;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDto request;
    private Show show;
    private Screen screen;
    private Seat seat;
    private Booking savedBooking;

    @BeforeEach
    void setUp() {
        request = BookingRequestDto.builder()
                .userId(1L)
                .showId(10L)
                .seats(List.of("A1", "A2"))
                .idempotencyKey("key-1")
                .build();

        screen = Screen.builder().id(100L).build();
        show = Show.builder()
                .id(10L)
                .price(new BigDecimal("250.00"))
                .screen(screen)
                .build();

        seat = Seat.builder()
                .id(1L)
                .seatNumber("A1")
                .build();

        savedBooking = Booking.builder()
                .id(1L)
                .userId(1L)
                .show(show)
                .status(BookingStatus.CONFIRMED)
                .totalAmount(new BigDecimal("500.00"))
                .idempotencyKey("key-1")
                .build();
    }

    @Test
    void createBooking_returnsExisting_whenIdempotencyKeyMatches() {
        when(bookingRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.of(savedBooking));

        BookingResponseDto result = bookingService.createBooking(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("500.00");
        verify(showRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_returnsResponse_whenSuccess() {
        Seat seatA2 = Seat.builder().id(2L).seatNumber("A2").build();
        when(bookingRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());
        when(showRepository.findById(10L)).thenReturn(Optional.of(show));
        when(seatRepository.findByScreenIdAndSeatNumber(100L, "A1")).thenReturn(Optional.of(seat));
        when(seatRepository.findByScreenIdAndSeatNumber(100L, "A2")).thenReturn(Optional.of(seatA2));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });
        when(discountEngine.apply(show, 2)).thenReturn(new BigDecimal("450.00"));

        BookingResponseDto result = bookingService.createBooking(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getShowId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("450.00");
        assertThat(result.getIdempotencyKey()).isEqualTo("key-1");
        verify(bookingRepository, times(2)).save(any(Booking.class));
        verify(seatBookingRepository, times(2)).save(any());
        verify(discountEngine).apply(show, 2);
    }

    @Test
    void createBooking_throws_whenShowNotFound() {
        when(bookingRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());
        when(showRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Show not found");
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_throwsSeatUnavailable_whenSeatAlreadyBooked() {
        when(bookingRepository.findByIdempotencyKey("key-1")).thenReturn(Optional.empty());
        when(showRepository.findById(10L)).thenReturn(Optional.of(show));
        when(seatRepository.findByScreenIdAndSeatNumber(100L, "A1")).thenReturn(Optional.of(seat));
        when(seatRepository.findByScreenIdAndSeatNumber(100L, "A2")).thenReturn(Optional.of(Seat.builder().id(2L).seatNumber("A2").build()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });
        when(seatBookingRepository.save(any())).thenThrow(new DataIntegrityViolationException("unique constraint"));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(SeatUnavailableException.class)
                .hasMessageContaining("already booked");
    }
}
