package com.booking.room_service.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long id) {
        super("Комната с id=" + id + " не найдена");
    }
}