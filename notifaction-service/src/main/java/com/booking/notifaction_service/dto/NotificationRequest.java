package com.booking.notifaction_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(

        @NotNull(message = "userId обязателен")
        Long userId,

        @NotBlank(message = "Email обязателен")
        @Email(message = "Некорректный email")
        String email,

        @NotBlank(message = "Тип уведомления обязателен")
        String type,

        @NotBlank(message = "Сообщение обязательно")
        String message
) {
}