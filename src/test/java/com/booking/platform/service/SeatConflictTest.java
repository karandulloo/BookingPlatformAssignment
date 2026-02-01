package com.booking.platform.service;

import com.booking.platform.dto.BookingRequestDto;
import com.booking.platform.exception.SeatUnavailableException;
import com.booking.platform.model.entity.City;
import com.booking.platform.model.entity.Movie;
import com.booking.platform.model.entity.Screen;
import com.booking.platform.model.entity.Seat;
import com.booking.platform.model.entity.Show;
import com.booking.platform.model.entity.Theatre;
import com.booking.platform.repository.CityRepository;
import com.booking.platform.repository.MovieRepository;
import com.booking.platform.repository.ScreenRepository;
import com.booking.platform.repository.SeatRepository;
import com.booking.platform.repository.ShowRepository;
import com.booking.platform.repository.TheatreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class SeatConflictTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private TheatreRepository theatreRepository;
    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ShowRepository showRepository;

    private Show show;
    private Seat seat;

    @BeforeEach
    void setUp() {
        City city = cityRepository.save(City.builder().name("TestCity").build());
        Theatre theatre = theatreRepository.save(Theatre.builder().name("Theatre1").address("Address1").city(city).build());
        Screen screen = screenRepository.save(Screen.builder().name("Screen1").theatre(theatre).build());
        seat = seatRepository.save(Seat.builder().seatNumber("A1").screen(screen).build());
        Movie movie = movieRepository.save(Movie.builder().title("Movie1").build());
        show = showRepository.save(Show.builder()
                .movie(movie)
                .theatre(theatre)
                .screen(screen)
                .showDate(LocalDate.now())
                .startTime(LocalTime.of(14, 0))
                .price(new BigDecimal("100.00"))
                .build());
    }

    @Test
    void createBooking_throwsSeatUnavailable_whenSameSeatBookedTwice() {
        BookingRequestDto first = BookingRequestDto.builder()
                .userId(1L)
                .showId(show.getId())
                .seats(List.of("A1"))
                .idempotencyKey("key-first")
                .build();
        bookingService.createBooking(first);

        BookingRequestDto second = BookingRequestDto.builder()
                .userId(2L)
                .showId(show.getId())
                .seats(List.of("A1"))
                .idempotencyKey("key-second")
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(second))
                .isInstanceOf(SeatUnavailableException.class)
                .hasMessageContaining("already booked");
    }
}
