package com.booking.booking_service.controller;

import com.booking.booking_service.dto.BookingRequest;
import com.booking.booking_service.dto.BookingResponse;
import com.booking.booking_service.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request,
                                                  HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        String userEmail = extractUserEmail(httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(userId, userEmail, request));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> findMyBookings(HttpServletRequest httpRequest) {
        Long userId = extractUserId(httpRequest);
        return ResponseEntity.ok(bookingService.findMyBookings(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> findById(@PathVariable Long id,
                                                    HttpServletRequest httpRequest,
                                                    Authentication authentication) {
        Long userId = extractUserId(httpRequest);
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(bookingService.findById(id, userId, isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id,
                                       HttpServletRequest httpRequest,
                                       Authentication authentication) {
        Long userId = extractUserId(httpRequest);
        boolean isAdmin = isAdmin(authentication);
        bookingService.cancel(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    private String extractUserEmail(HttpServletRequest request) {
        return (String) request.getAttribute("userEmail");
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}