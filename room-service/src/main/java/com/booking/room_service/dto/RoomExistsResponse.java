package com.booking.room_service.dto;


public record RoomExistsResponse(
        boolean exists,
        Long roomId,
        Integer capacity
) {
}