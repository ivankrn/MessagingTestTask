package ru.ivankrn.messagingtesttask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequestDTO(
        @NotBlank
        String login,
        @NotBlank
        String password,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String fio
) {
}
