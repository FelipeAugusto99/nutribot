package br.com.fiap.nutribot.ai;

import jakarta.validation.constraints.NotBlank;

public record MealAnalysisRequest(
        @NotBlank(message = "description is required") String description
) {}
