package com.booking.booking_service.exception;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(Integer requested, Integer capacity) {
        super("Запрошено мест: " + requested + ", вместимость комнаты: " + capacity);
    }
}