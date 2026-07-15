package com.booking.booking_service.client;

import com.booking.booking_service.dto.RoomExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-service", url = "${room-service.url}")
public interface RoomClient {

    @GetMapping("/internal/rooms/{id}/exists")
    RoomExistsResponse checkExists(@PathVariable("id") Long id);
}