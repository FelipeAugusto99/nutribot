package br.com.fiap.nutribot.ai;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import br.com.fiap.nutribot.dto.MealEntryResponse;
import br.com.fiap.nutribot.dto.SummaryResponse;
import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.service.FoodService;
import br.com.fiap.nutribot.service.MealService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

// Tools do /chat: reaproveitam FoodService e MealService do projeto base
@Component
public class NutriBotTools {

    private final FoodService foodService;
    private final MealService mealService;

    public NutriBotTools(FoodService foodService, MealService mealService) {
        this.foodService = foodService;
        this.mealService = mealService;
    }

    @Tool(description = """
            Busca alimentos na tabela da Clínica Equilíbrio pelo nome e devolve calorias (kcal), proteínas,
            carboidratos e gorduras (g) por 100 g. Use sempre que perguntarem valores nutricionais de um alimento.""")
    public List<FoodInfo> buscarAlimento(
            @ToolParam(description = "Nome do alimento, ex.: tapioca, arroz, ovo") String nome) {
        return search(nome).stream().limit(5).map(FoodInfo::from).toList();
    }

    @Tool(description = """
            Resumo do diário alimentar do paciente em um dia: calorias consumidas, proteínas, carboidratos,
            gorduras, meta diária (dailyCalories), calorias que ainda faltam (remainingCalories) e as refeições.
            Use para perguntas como "quantas calorias ainda posso comer hoje?".""")
    public SummaryResponse resumoDoDia(
            @ToolParam(required = false, description = "Data no formato yyyy-MM-dd; vazio para hoje") String data) {
        return mealService.summary(parseDate(data));
    }

    @Tool(description = """
            Registra no diário alimentar de hoje uma refeição que o paciente comeu. Grava os dados.
            Chame uma vez para cada alimento.""")
    public RegisterResult registrarRefeicao(
            @ToolParam(description = "Nome do alimento como está na tabela, ex.: Ovo cozido, Tapioca") String alimento,
            @ToolParam(description = "Quantidade em gramas, maior que zero (1 ovo = 50 g)") double gramas,
            @ToolParam(description = "BREAKFAST (café da manhã), LUNCH (almoço), DINNER (jantar) ou SNACK (lanche)")
            String tipoRefeicao) {
        MealType mealType = parseMealType(tipoRefeicao);
        if (mealType == null) {
            return RegisterResult.error("Tipo de refeição inválido: use BREAKFAST, LUNCH, DINNER ou SNACK");
        }
        List<Food> foods = search(alimento);
        if (foods.isEmpty()) {
            return RegisterResult.error("Alimento não encontrado na tabela: " + alimento);
        }
        try {
            return RegisterResult.ok(MealEntryResponse.from(mealService.log(foods.getFirst().getName(), gramas, mealType)));
        } catch (ResponseStatusException ex) {
            return RegisterResult.error(ex.getReason());
        }
    }

    // tenta o nome inteiro e, se não achar, cada palavra no singular ("2 ovos mexidos" -> "ovo")
    private List<Food> search(String name) {
        if (name == null || name.isBlank()) {
            return List.of();
        }
        Set<String> attempts = new LinkedHashSet<>();
        attempts.add(name.trim());
        for (String word : name.trim().toLowerCase(Locale.ROOT).split("[\\s,]+")) {
            if (word.length() > 2 && !word.matches("\\d+.*|de|com|sem|do|da")) {
                attempts.add(word.endsWith("s") ? word.substring(0, word.length() - 1) : word);
            }
        }
        for (String attempt : attempts) {
            List<Food> found = foodService.search(attempt);
            if (!found.isEmpty()) {
                return new ArrayList<>(found);
            }
        }
        return List.of();
    }

    private static LocalDate parseDate(String data) {
        try {
            return data == null || data.isBlank() ? LocalDate.now() : LocalDate.parse(data.trim());
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private static MealType parseMealType(String type) {
        try {
            return MealType.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            return null;
        }
    }

    public record FoodInfo(Long id, String name, double caloriesPer100g, double proteinPer100g,
                           double carbsPer100g, double fatPer100g) {

        static FoodInfo from(Food food) {
            return new FoodInfo(food.getId(), food.getName(), food.getCalories(), food.getProtein(),
                    food.getCarbs(), food.getFat());
        }
    }

    public record RegisterResult(boolean registered, MealEntryResponse meal, String error) {

        static RegisterResult ok(MealEntryResponse meal) {
            return new RegisterResult(true, meal, null);
        }

        static RegisterResult error(String error) {
            return new RegisterResult(false, null, error);
        }
    }
}
