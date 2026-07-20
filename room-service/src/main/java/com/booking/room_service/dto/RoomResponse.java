package com.booking.room_service.dto;

import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        String name,
        Integer capacity,
        String description,
        String location,
        Boolean isActive,
        LocalDateTime createdAt
) {
}