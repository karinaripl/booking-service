package com.booking.notifaction_service.kafka;

import com.booking.notifaction_service.dto.NotificationRequest;
import com.booking.notifaction_service.service.EmailService;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public NotificationEventListener(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${kafka.topic.booking-notifications}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(String message) {
        try {
            NotificationRequest request = objectMapper.readValue(message, NotificationRequest.class);

            log.info("Уведомление принято из Kafka: userId={}, type={}, message={}",
                    request.userId(), request.type(), request.message());

            emailService.send(request.email(), request.type(), request.message());
        } catch (Exception e) {
            log.error("Не удалось обработать сообщение из Kafka: {}", message, e);
        }
    }
}