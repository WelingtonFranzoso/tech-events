package com.franzoso.domain.coupon;

public record CouponRequestDTO(String code, Integer discount, Long valid) {
}
