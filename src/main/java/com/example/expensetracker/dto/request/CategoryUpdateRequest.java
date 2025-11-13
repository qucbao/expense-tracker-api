package com.example.expensetracker.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description
) {}
