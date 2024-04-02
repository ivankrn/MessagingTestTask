package ru.ivankrn.messagingtesttask.dto;

import jakarta.validation.constraints.NotBlank;

public record ApprovalRequestDTO(@NotBlank String email) {
}
