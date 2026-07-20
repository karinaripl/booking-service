package com.booking.room_service.mapper;

import com.booking.model.generated.room.tables.records.RoomsRecord;
import com.booking.room_service.dto.RoomResponse;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public RoomResponse toResponse(RoomsRecord record) {
        return new RoomResponse(
                record.getId(),
                record.getName(),
                record.getCapacity(),
                record.getDescription(),
                record.getLocation(),
                record.getIsActive(),
                record.getCreatedAt()
        );
    }
}