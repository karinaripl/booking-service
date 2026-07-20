package com.booking.booking_service.mapper;

import com.booking.booking_service.dto.BookingResponse;
import com.booking.model.generated.booking.tables.records.BookingRecord;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(BookingRecord record) {
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