package com.booking.booking_service.kafka;

import com.booking.booking_service.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class NotificationEventProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.booking-notifications}")
    private String topic;

    public NotificationEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(NotificationRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(topic, json);
        } catch (JacksonException e) {
            log.warn("Не удалось сериализовать уведомление userId={}, type={}: {}",
                    request.userId(), request.type(), e.getMessage());
        }
    }
}