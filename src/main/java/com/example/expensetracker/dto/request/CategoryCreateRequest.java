package com.example.expensetracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "Name is required") String name,

        String description) {
}
