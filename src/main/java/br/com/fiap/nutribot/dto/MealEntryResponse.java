package br.com.fiap.nutribot.dto;

import java.time.LocalDate;

import br.com.fiap.nutribot.model.MealEntry;
import br.com.fiap.nutribot.model.MealType;

public record MealEntryResponse(
        Long id,
        LocalDate date,
        MealType mealType,
        Long foodId,
        String foodName,
        double grams,
        double calories
) {

    public static MealEntryResponse from(MealEntry entry) {
        return new MealEntryResponse(entry.getId(), entry.getDate(), entry.getMealType(),
                entry.getFood().getId(), entry.getFood().getName(), entry.getGrams(),
                Numbers.round(entry.calories()));
    }
}
