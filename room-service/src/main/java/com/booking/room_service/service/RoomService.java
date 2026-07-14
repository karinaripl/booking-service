package com.booking.room_service.service;

import com.booking.model.generated.room.tables.records.RoomsRecord;
import com.booking.room_service.dto.RoomExistsResponse;
import com.booking.room_service.dto.RoomRequest;
import com.booking.room_service.dto.RoomResponse;
import com.booking.room_service.exception.RoomNotFoundException;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.booking.model.generated.room.tables.Rooms.ROOMS;

@Service
public class RoomService {

    private final DSLContext dsl;

    public RoomService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public RoomResponse create(RoomRequest request) {
        RoomsRecord record = dsl.insertInto(ROOMS)
                .set(ROOMS.NAME, request.name())
                .set(ROOMS.CAPACITY, request.capacity())
                .set(ROOMS.DESCRIPTION, request.description())
                .set(ROOMS.LOCATION, request.location())
                .set(ROOMS.IS_ACTIVE, true)
                .returning()
                .fetchOne();

        return toResponse(record);
    }

    public List<RoomResponse> findAll() {
        return dsl.selectFrom(ROOMS)
                .where(ROOMS.IS_ACTIVE.isTrue())
                .fetch()
                .map(this::toResponse);
    }

    public RoomResponse findById(Long id) {
        RoomsRecord record = dsl.selectFrom(ROOMS)
                .where(ROOMS.ID.eq(id))
                .fetchOne();

        if (record == null) {
            throw new RoomNotFoundException(id);
        }
        return toResponse(record);
    }

    public RoomResponse update(Long id, RoomRequest request) {
        int updated = dsl.update(ROOMS)
                .set(ROOMS.NAME, request.name())
                .set(ROOMS.CAPACITY, request.capacity())
                .set(ROOMS.DESCRIPTION, request.description())
                .set(ROOMS.LOCATION, request.location())
                .where(ROOMS.ID.eq(id))
                .execute();

        if (updated == 0) {
            throw new RoomNotFoundException(id);
        }
        return findById(id);
    }


    public void delete(Long id) {
        int updated = dsl.update(ROOMS)
                .set(ROOMS.IS_ACTIVE, false)
                .where(ROOMS.ID.eq(id))
                .execute();

        if (updated == 0) {
            throw new RoomNotFoundException(id);
        }
    }

    public RoomExistsResponse checkExists(Long id) {
        RoomsRecord record = dsl.selectFrom(ROOMS)
                .where(ROOMS.ID.eq(id))
                .and(ROOMS.IS_ACTIVE.isTrue())
                .fetchOne();

        if (record == null) {
            return new RoomExistsResponse(false, id, null);
        }
        return new RoomExistsResponse(true, id, record.getCapacity());
    }

    private RoomResponse toResponse(RoomsRecord record) {
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