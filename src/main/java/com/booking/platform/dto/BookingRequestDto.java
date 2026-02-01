package com.booking.platform.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequestDto {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "showId is required")
    private Long showId;

    @NotEmpty(message = "at least one seat is required")
    private List<String> seats;

    @NotNull(message = "idempotencyKey is required")
    private String idempotencyKey;
}
