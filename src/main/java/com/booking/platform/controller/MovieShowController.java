package com.booking.platform.controller;

import com.booking.platform.dto.TheatreShowsDto;
import com.booking.platform.service.ShowQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Movie Shows", description = "Browse shows for a movie by city and date")
public class MovieShowController {

    private final ShowQueryService showQueryService;

    @GetMapping(value = "/{movieId}/shows", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get shows by movie, city and date",
            description = "Returns shows for the given movie in the given city on the given date, grouped by theatre."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = TheatreShowsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request (e.g. invalid date format)")
    })
    public ResponseEntity<List<TheatreShowsDto>> getShows(
            @Parameter(description = "Movie ID", required = true) @PathVariable Long movieId,
            @Parameter(description = "City name", required = true) @RequestParam String city,
            @Parameter(description = "Show date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TheatreShowsDto> body = showQueryService.getShowsByMovieCityAndDate(movieId, city, date);
        return ResponseEntity.ok(body);
    }
}
