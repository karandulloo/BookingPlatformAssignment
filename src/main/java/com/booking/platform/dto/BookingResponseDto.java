package com.booking.platform.dto;

import com.booking.platform.model.entity.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponseDto {

    private Long id;
    private Long userId;
    private Long showId;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String idempotencyKey;
}
