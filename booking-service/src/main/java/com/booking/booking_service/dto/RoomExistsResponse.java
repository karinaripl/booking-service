package com.booking.booking_service.dto;

public record RoomExistsResponse(
        boolean exists,
        Long roomId,
        Integer capacity
) {
}