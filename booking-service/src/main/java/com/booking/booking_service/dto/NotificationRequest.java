package com.booking.booking_service.dto;

public record NotificationRequest(
        Long userId,
        String email,
        String type,
        String message
) {
}