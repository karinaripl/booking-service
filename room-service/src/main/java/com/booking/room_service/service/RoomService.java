package com.booking.room_service.service;

import com.booking.model.generated.room.tables.records.RoomsRecord;
import com.booking.room_service.dto.RoomExistsResponse;
import com.booking.room_service.dto.RoomRequest;
import com.booking.room_service.dto.RoomResponse;
import com.booking.room_service.exception.RoomNotFoundException;
import com.booking.room_service.mapper.RoomMapper;
import com.booking.room_service.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    public RoomResponse create(RoomRequest request) {
        RoomsRecord record = roomRepository.insert(
                request.name(), request.capacity(), request.description(), request.location());
        return roomMapper.toResponse(record);
    }

    public List<RoomResponse> findAll() {
        return roomRepository.findAllActive().stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    public RoomResponse findById(Long id) {
        RoomsRecord record = roomRepository.findById(id);
        if (record == null) {
            throw new RoomNotFoundException(id);
        }
        return roomMapper.toResponse(record);
    }

    public RoomResponse update(Long id, RoomRequest request) {
        int updated = roomRepository.update(
                id, request.name(), request.capacity(), request.description(), request.location());
        if (updated == 0) {
            throw new RoomNotFoundException(id);
        }
        return findById(id);
    }

    public void delete(Long id) {
        int updated = roomRepository.deactivate(id);
        if (updated == 0) {
            throw new RoomNotFoundException(id);
        }
    }

    public RoomExistsResponse checkExists(Long id) {
        RoomsRecord record = roomRepository.findActiveById(id);
        if (record == null) {
            return new RoomExistsResponse(false, id, null);
        }
        return new RoomExistsResponse(true, id, record.getCapacity());
    }
}