package br.com.fiap.nutribot.service;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.nutribot.dto.MealEntryRequest;
import br.com.fiap.nutribot.dto.SummaryResponse;
import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.model.MealEntry;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.repository.MealEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealEntryRepository repository;
    private final FoodService foodService;
    private final ProfileService profileService;

    public List<MealEntry> findByDate(LocalDate date) {
        return repository.findByDateOrderByMealTypeAscIdAsc(date == null ? LocalDate.now() : date);
    }

    public MealEntry create(MealEntryRequest request) {
        Food food = foodService.findById(request.foodId());
        return save(request.date(), request.mealType(), food, request.grams());
    }

    // registra pelo nome do alimento, ex.: ("ovo", 100, BREAKFAST) — pronto para ser usado por uma tool
    public MealEntry log(String foodName, double grams, MealType mealType) {
        if (grams <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "grams must be positive");
        }
        return save(null, mealType, foodService.findByName(foodName), grams);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meal not found");
        }
        repository.deleteById(id);
    }

    public SummaryResponse summary(LocalDate date) {
        LocalDate day = date == null ? LocalDate.now() : date;
        return SummaryResponse.of(day, findByDate(day), profileService.get());
    }

    private MealEntry save(LocalDate date, MealType mealType, Food food, double grams) {
        MealEntry entry = MealEntry.builder()
                .date(date == null ? LocalDate.now() : date)
                .mealType(mealType)
                .food(food)
                .grams(grams)
                .build();
        return repository.save(entry);
    }
}
