package com.booking.platform.service.discount;

import com.booking.platform.model.entity.Show;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DiscountEngine {

    private final List<DiscountRule> rules;

    @Autowired(required = false)
    public DiscountEngine(List<DiscountRule> rules) {
        this.rules = rules != null ? rules : Collections.emptyList();
    }

    public BigDecimal apply(Show show, int seatCount) {
        BigDecimal amount = show.getPrice().multiply(BigDecimal.valueOf(seatCount));
        List<DiscountRule> ordered = rules.stream()
                .sorted(Comparator.comparingInt(this::getOrder))
                .collect(Collectors.toList());
        for (DiscountRule rule : ordered) {
            amount = rule.apply(show, seatCount, amount);
        }
        return amount;
    }

    private int getOrder(DiscountRule rule) {
        Order order = rule.getClass().getAnnotation(Order.class);
        return order != null ? order.value() : 0;
    }
}
