package com.booking.platform.service;

import com.booking.platform.dto.ShowSummaryDto;
import com.booking.platform.dto.TheatreShowsDto;
import com.booking.platform.model.entity.Show;
import com.booking.platform.repository.CityRepository;
import com.booking.platform.repository.SeatBookingRepository;
import com.booking.platform.repository.SeatRepository;
import com.booking.platform.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowQueryService {

    private final CityRepository cityRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final SeatBookingRepository seatBookingRepository;

    @Transactional(readOnly = true)
    public List<TheatreShowsDto> getShowsByMovieCityAndDate(Long movieId, String city, LocalDate date) {
        var cityEntity = cityRepository.findByNameIgnoreCase(city);
        if (cityEntity.isEmpty()) {
            return List.of();
        }
        Long cityId = cityEntity.get().getId();

        List<Show> shows = showRepository.findByMovieIdAndTheatre_City_IdAndShowDate(movieId, cityId, date);
        if (shows.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Show>> byTheatre = new LinkedHashMap<>();
        for (Show show : shows) {
            Long theatreId = show.getTheatre().getId();
            byTheatre.computeIfAbsent(theatreId, k -> new ArrayList<>()).add(show);
        }

        List<TheatreShowsDto> result = new ArrayList<>();
        for (Map.Entry<Long, List<Show>> entry : byTheatre.entrySet()) {
            List<Show> theatreShows = entry.getValue();
            Show first = theatreShows.get(0);
            String theatreName = first.getTheatre().getName();
            String address = first.getTheatre().getAddress();

            List<ShowSummaryDto> showDtos = theatreShows.stream()
                    .map(this::toShowSummaryDto)
                    .collect(Collectors.toList());

            result.add(TheatreShowsDto.builder()
                    .theatreName(theatreName)
                    .address(address)
                    .shows(showDtos)
                    .build());
        }
        return result;
    }

    private ShowSummaryDto toShowSummaryDto(Show show) {
        long totalSeats = seatRepository.countByScreenId(show.getScreen().getId());
        long bookedSeats = seatBookingRepository.countByShowId(show.getId());
        int availableSeats = (int) Math.max(0, totalSeats - bookedSeats);

        return ShowSummaryDto.builder()
                .showId(show.getId())
                .startTime(show.getStartTime())
                .price(show.getPrice())
                .availableSeats(availableSeats)
                .build();
    }
}
