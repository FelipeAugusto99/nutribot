package br.com.fiap.nutribot.dto;

import br.com.fiap.nutribot.model.Goal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProfileRequest(
        @NotBlank(message = "name is required") String name,
        @NotNull(message = "goal is required") Goal goal,
        @NotNull(message = "dailyCalories is required")
        @Min(value = 1000, message = "dailyCalories must be at least 1000")
        @Max(value = 5000, message = "dailyCalories must be at most 5000") Integer dailyCalories
) {}
