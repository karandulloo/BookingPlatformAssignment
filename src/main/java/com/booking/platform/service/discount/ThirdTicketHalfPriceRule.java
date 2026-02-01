package com.booking.platform.service.discount;

import com.booking.platform.model.entity.Show;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Order(1)
public class ThirdTicketHalfPriceRule implements DiscountRule {

    private static final BigDecimal HALF = new BigDecimal("0.5");

    @Override
    public BigDecimal apply(Show show, int seatCount, BigDecimal currentAmount) {
        if (seatCount < 3) {
            return currentAmount;
        }
        int thirdTicketsCount = seatCount / 3;
        BigDecimal pricePerSeat = currentAmount.divide(BigDecimal.valueOf(seatCount), 2, RoundingMode.HALF_UP);
        BigDecimal discountPerThirdTicket = pricePerSeat.multiply(HALF);
        BigDecimal discount = discountPerThirdTicket.multiply(BigDecimal.valueOf(thirdTicketsCount));
        return currentAmount.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }
}
