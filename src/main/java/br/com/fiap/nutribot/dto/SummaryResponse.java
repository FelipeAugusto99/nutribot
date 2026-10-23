package br.com.fiap.nutribot.dto;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.nutribot.model.MealEntry;
import br.com.fiap.nutribot.model.Profile;

// Resumo de um dia: o que foi consumido e quanto falta para a meta de calorias
public record SummaryResponse(
        LocalDate date,
        double consumedCalories,
        double protein,
        double carbs,
        double fat,
        int dailyCalories,
        double remainingCalories,
        List<MealEntryResponse> meals
) {

    public static SummaryResponse of(LocalDate date, List<MealEntry> entries, Profile profile) {
        double calories = 0, protein = 0, carbs = 0, fat = 0;
        for (MealEntry entry : entries) {
            double factor = entry.getGrams() / 100;
            calories += entry.getFood().getCalories() * factor;
            protein += entry.getFood().getProtein() * factor;
            carbs += entry.getFood().getCarbs() * factor;
            fat += entry.getFood().getFat() * factor;
        }
        return new SummaryResponse(date, Numbers.round(calories), Numbers.round(protein), Numbers.round(carbs),
                Numbers.round(fat), profile.getDailyCalories(), Numbers.round(profile.getDailyCalories() - calories),
                entries.stream().map(MealEntryResponse::from).toList());
    }
}
