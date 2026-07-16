package com.booking.booking_service.service;

import com.booking.booking_service.client.NotificationClient;
import com.booking.booking_service.client.RoomClient;
import com.booking.booking_service.dto.BookingRequest;
import com.booking.booking_service.dto.BookingResponse;
import com.booking.booking_service.dto.NotificationRequest;
import com.booking.booking_service.dto.RoomExistsResponse;
import com.booking.booking_service.exception.BookingNotFoundException;
import com.booking.booking_service.exception.CapacityExceededException;
import com.booking.booking_service.exception.RoomNotFoundException;
import com.booking.model.generated.booking.tables.records.BookingRecord;
import feign.FeignException;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.booking.model.generated.booking.tables.Booking.BOOKING;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final DSLContext dsl;
    private final RoomClient roomClient;
    private final NotificationClient notificationClient;

    public BookingService(DSLContext dsl, RoomClient roomClient, NotificationClient notificationClient) {
        this.dsl = dsl;
        this.roomClient = roomClient;
        this.notificationClient = notificationClient;
    }

    public BookingResponse create(Long userId, BookingRequest request) {
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

        BookingRecord record = dsl.insertInto(BOOKING)
                .set(BOOKING.USER_ID, userId)
                .set(BOOKING.ROOM_ID, request.roomId())
                .set(BOOKING.START_TIME, request.startTime())
                .set(BOOKING.END_TIME, request.endTime())
                .set(BOOKING.STATUS, "PENDING")
                .returning()
                .fetchOne();

        notifySafely(userId, "BOOKING_CREATED",
                "Ваша бронь на " + request.startTime() + " создана и ожидает подтверждения");

        return toResponse(record);
    }

    public List<BookingResponse> findMyBookings(Long userId) {
        return dsl.selectFrom(BOOKING)
                .where(BOOKING.USER_ID.eq(userId))
                .orderBy(BOOKING.START_TIME.desc())
                .fetch()
                .map(this::toResponse);
    }

    public BookingResponse findById(Long id, Long userId, boolean isAdmin) {
        BookingRecord record = dsl.selectFrom(BOOKING)
                .where(BOOKING.ID.eq(id))
                .fetchOne();

        if (record == null || (!isAdmin && !record.getUserId().equals(userId))) {
            throw new BookingNotFoundException(id);
        }
        return toResponse(record);
    }

    public void cancel(Long id, Long userId, boolean isAdmin) {
        BookingRecord record = dsl.selectFrom(BOOKING)
                .where(BOOKING.ID.eq(id))
                .fetchOne();

        if (record == null || (!isAdmin && !record.getUserId().equals(userId))) {
            throw new BookingNotFoundException(id);
        }

        dsl.update(BOOKING)
                .set(BOOKING.STATUS, "CANCELLED")
                .where(BOOKING.ID.eq(id))
                .execute();

        notifySafely(record.getUserId(), "BOOKING_CANCELLED",
                "Ваша бронь на " + record.getStartTime() + " отменена");
    }

    private void notifySafely(Long userId, String type, String message) {
        try {
            notificationClient.send(new NotificationRequest(userId, type, message));
        } catch (Exception e) {
            log.warn("Не удалось отправить уведомление userId={}, type={}: {}", userId, type, e.getMessage());
        }
    }

    private BookingResponse toResponse(BookingRecord record) {
        return new BookingResponse(
                record.getId(),
                record.getUserId(),
                record.getRoomId(),
                record.getStartTime(),
                record.getEndTime(),
                record.getStatus(),
                record.getCreatedAt()
        );
    }
}