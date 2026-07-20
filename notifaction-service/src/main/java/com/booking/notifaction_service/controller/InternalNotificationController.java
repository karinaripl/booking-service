package com.booking.notifaction_service.controller;

import com.booking.notifaction_service.dto.NotificationRequest;
import com.booking.notifaction_service.service.EmailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/notifications")
public class InternalNotificationController {

    private static final Logger log = LoggerFactory.getLogger(InternalNotificationController.class);

    private final EmailService emailService;

    public InternalNotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<Void> accept(@Valid @RequestBody NotificationRequest request) {
        log.info("Уведомление принято: userId={}, type={}, message={}",
                request.userId(), request.type(), request.message());

        emailService.send(request.email(), request.type(), request.message());

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}