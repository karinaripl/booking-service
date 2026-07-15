package com.booking.booking_service.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Бронирование с id=" + id + " не найдено");
    }
}