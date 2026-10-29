package br.com.fiap.nutribot.ai;

import java.util.List;

/** Saída estruturada da estimativa. A análise não grava nada no diário alimentar. */
public record MealAnalysisResponse(
        List<Item> items,
        double totalCalories,
        double protein,
        double carbs,
        double fat
) {
    public record Item(String food, double grams, double calories) {}
}
