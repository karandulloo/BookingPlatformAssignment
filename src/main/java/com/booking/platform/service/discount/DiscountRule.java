package com.booking.platform.service.discount;

import com.booking.platform.model.entity.Show;

import java.math.BigDecimal;

public interface DiscountRule {

    BigDecimal apply(Show show, int seatCount, BigDecimal currentAmount);
}
