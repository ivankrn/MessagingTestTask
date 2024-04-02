package ru.ivankrn.messagingtesttask.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(
        @NotBlank
        String login,
        @NotBlank
        String password
        ) {
}
