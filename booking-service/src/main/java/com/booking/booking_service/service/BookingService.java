package com.booking.booking_service.service;

import com.booking.booking_service.client.RoomClient;
import com.booking.booking_service.dto.BookingRequest;
import com.booking.booking_service.dto.BookingResponse;
import com.booking.booking_service.dto.NotificationRequest;
import com.booking.booking_service.dto.RoomExistsResponse;
import com.booking.booking_service.exception.BookingNotFoundException;
import com.booking.booking_service.exception.CapacityExceededException;
import com.booking.booking_service.exception.RoomNotFoundException;
import com.booking.booking_service.kafka.NotificationEventProducer;
import com.booking.booking_service.mapper.BookingMapper;
import com.booking.booking_service.repository.BookingRepository;
import com.booking.model.generated.booking.tables.records.BookingRecord;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final RoomClient roomClient;
    private final NotificationEventProducer notificationEventProducer;

    public BookingService(BookingRepository bookingRepository,
                          BookingMapper bookingMapper,
                          RoomClient roomClient,
                          NotificationEventProducer notificationEventProducer) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.roomClient = roomClient;
        this.notificationEventProducer = notificationEventProducer;
    }

    public BookingResponse create(Long userId, String userEmail, BookingRequest request) {
        RoomExistsResponse room;
        try {
            room = roomClient.checkExists(request.roomId());
        } catch (FeignException.NotFound e) {
            throw new RoomNotFoundException(request.roomId());
        }

        if (!room.exists()) {
            throw new RoomNotFoundException(request.roomId());
        }

        if (request.attendeesCount() > room.capacity()) {
            throw new CapacityExceededException(request.attendeesCount(), room.capacity());
        }

        BookingRecord record = bookingRepository.insert(
                userId, request.roomId(), request.startTime(), request.endTime());

        notifySafely(userId, userEmail, "BOOKING_CREATED",
                "Ваша бронь на " + request.startTime() + " создана и ожидает подтверждения");

        return bookingMapper.toResponse(record);
    }

    public List<BookingResponse> findMyBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    public BookingResponse findById(Long id, Long userId, boolean isAdmin) {
        BookingRecord record = bookingRepository.findById(id);
        if (record == null || (!isAdmin && !record.getUserId().equals(userId))) {
            throw new BookingNotFoundException(id);
        }
        return bookingMapper.toResponse(record);
    }

    public void cancel(Long id, Long userId, boolean isAdmin) {
        BookingRecord record = bookingRepository.findById(id);
        if (record == null || (!isAdmin && !record.getUserId().equals(userId))) {
            throw new BookingNotFoundException(id);
        }

        bookingRepository.cancel(id);

        notifySafely(record.getUserId(), null, "BOOKING_CANCELLED",
                "Ваша бронь на " + record.getStartTime() + " отменена");
    }

    private void notifySafely(Long userId, String userEmail, String type, String message) {
        try {
            notificationEventProducer.send(new NotificationRequest(userId, userEmail, type, message));
        } catch (Exception e) {
            log.warn("Не удалось отправить уведомление userId={}, type={}: {}", userId, type, e.getMessage());
        }
    }
}