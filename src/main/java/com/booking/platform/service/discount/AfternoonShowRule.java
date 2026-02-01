package com.booking.platform.service.discount;

import com.booking.platform.model.entity.Show;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

@Component
@Order(2)
public class AfternoonShowRule implements DiscountRule {

    private static final LocalTime AFTERNOON_END = LocalTime.of(17, 0);
    private static final BigDecimal AFTERNOON_DISCOUNT = new BigDecimal("0.20");

    @Override
    public BigDecimal apply(Show show, int seatCount, BigDecimal currentAmount) {
        if (show.getStartTime().isBefore(AFTERNOON_END)) {
            BigDecimal discount = currentAmount.multiply(AFTERNOON_DISCOUNT);
            return currentAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return currentAmount;
    }
}
