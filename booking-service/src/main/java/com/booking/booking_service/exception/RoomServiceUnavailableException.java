package com.booking.booking_service.exception;

public class RoomServiceUnavailableException extends RuntimeException {
    public RoomServiceUnavailableException() {
        super("Сервис комнат временно недоступен, попробуйте позже");
    }
}