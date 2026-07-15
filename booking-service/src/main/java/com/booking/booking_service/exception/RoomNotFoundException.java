package com.booking.booking_service.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(Long roomId) {
        super("Комната с id=" + roomId + " не найдена");
    }
}