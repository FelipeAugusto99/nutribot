package br.com.fiap.nutribot.data;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.model.Goal;
import br.com.fiap.nutribot.model.MealEntry;
import br.com.fiap.nutribot.model.MealType;
import br.com.fiap.nutribot.model.Profile;
import br.com.fiap.nutribot.repository.FoodRepository;
import br.com.fiap.nutribot.repository.MealEntryRepository;
import br.com.fiap.nutribot.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Dados iniciais: tabela de alimentos (valores aproximados por 100 g, base TACO),
// o perfil do paciente e algumas refeições de hoje e de ontem
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final FoodRepository foodRepository;
    private final MealEntryRepository mealRepository;
    private final ProfileRepository profileRepository;

    @Override
    public void run(String... args) {
        foodRepository.saveAll(List.of(
                food("Arroz branco cozido", "Cereais", 128, 2.5, 28.1, 0.2),
                food("Arroz integral cozido", "Cereais", 124, 2.6, 25.8, 1.0),
                food("Feijão carioca cozido", "Leguminosas", 76, 4.8, 13.6, 0.5),
                food("Feijão preto cozido", "Leguminosas", 77, 4.5, 14.0, 0.5),
                food("Lentilha cozida", "Leguminosas", 93, 6.3, 16.3, 0.5),
                food("Grão-de-bico cozido", "Leguminosas", 164, 8.9, 27.4, 2.6),
                food("Macarrão cozido", "Cereais", 158, 5.8, 30.9, 0.9),
                food("Pão francês", "Pães", 300, 8.0, 58.6, 3.1),
                food("Pão de forma integral", "Pães", 253, 9.4, 49.9, 3.7),
                food("Tapioca", "Cereais", 240, 0.0, 60.0, 0.0),
                food("Cuscuz de milho cozido", "Cereais", 113, 2.2, 25.3, 0.7),
                food("Aveia em flocos", "Cereais", 394, 13.9, 66.6, 8.5),
                food("Batata inglesa cozida", "Tubérculos", 52, 1.2, 11.9, 0.0),
                food("Batata-doce cozida", "Tubérculos", 77, 0.6, 18.4, 0.1),
                food("Mandioca cozida", "Tubérculos", 125, 0.6, 30.1, 0.3),
                food("Peito de frango grelhado", "Carnes", 159, 32.0, 0.0, 2.5),
                food("Coxa de frango assada", "Carnes", 215, 28.5, 0.0, 10.4),
                food("Patinho moído refogado", "Carnes", 219, 35.9, 0.0, 7.3),
                food("Alcatra grelhada", "Carnes", 241, 31.9, 0.0, 11.6),
                food("Carne de porco lombo assado", "Carnes", 210, 35.7, 0.0, 6.4),
                food("Tilápia grelhada", "Peixes", 128, 26.2, 0.0, 2.7),
                food("Salmão grelhado", "Peixes", 229, 23.9, 0.0, 14.0),
                food("Atum em lata (água)", "Peixes", 116, 25.5, 0.0, 0.8),
                food("Sardinha em lata (óleo)", "Peixes", 285, 24.6, 0.0, 20.5),
                food("Ovo cozido", "Ovos", 146, 13.3, 0.6, 9.5),
                food("Ovo frito", "Ovos", 240, 15.6, 1.2, 18.6),
                food("Leite integral", "Laticínios", 61, 3.2, 4.7, 3.3),
                food("Leite desnatado", "Laticínios", 35, 3.4, 4.9, 0.2),
                food("Iogurte natural", "Laticínios", 51, 4.1, 1.9, 3.0),
                food("Queijo minas frescal", "Laticínios", 264, 17.4, 3.2, 20.2),
                food("Queijo muçarela", "Laticínios", 330, 22.6, 3.0, 25.2),
                food("Requeijão", "Laticínios", 257, 9.6, 2.4, 23.4),
                food("Banana prata", "Frutas", 98, 1.3, 26.0, 0.1),
                food("Maçã", "Frutas", 56, 0.3, 15.2, 0.0),
                food("Laranja", "Frutas", 37, 1.0, 8.9, 0.1),
                food("Mamão papaia", "Frutas", 40, 0.5, 10.4, 0.1),
                food("Manga", "Frutas", 64, 0.4, 16.7, 0.3),
                food("Morango", "Frutas", 30, 0.9, 6.8, 0.3),
                food("Abacate", "Frutas", 96, 1.2, 6.0, 8.4),
                food("Melancia", "Frutas", 33, 0.9, 8.1, 0.0),
                food("Uva", "Frutas", 53, 0.7, 13.6, 0.2),
                food("Abacaxi", "Frutas", 48, 0.9, 12.3, 0.1),
                food("Alface", "Hortaliças", 11, 1.3, 1.7, 0.2),
                food("Tomate", "Hortaliças", 15, 1.1, 3.1, 0.2),
                food("Cenoura crua", "Hortaliças", 34, 1.3, 7.7, 0.2),
                food("Brócolis cozido", "Hortaliças", 25, 2.1, 4.4, 0.5),
                food("Abobrinha refogada", "Hortaliças", 24, 1.1, 4.3, 0.6),
                food("Pepino", "Hortaliças", 10, 0.9, 2.0, 0.0),
                food("Couve refogada", "Hortaliças", 90, 1.7, 8.7, 6.6),
                food("Beterraba cozida", "Hortaliças", 32, 1.3, 7.2, 0.1),
                food("Azeite de oliva", "Óleos", 884, 0.0, 0.0, 100.0),
                food("Manteiga", "Óleos", 726, 0.4, 0.1, 82.4),
                food("Castanha-do-pará", "Oleaginosas", 643, 14.5, 15.1, 63.5),
                food("Amendoim torrado", "Oleaginosas", 606, 22.5, 18.7, 54.0),
                food("Pasta de amendoim", "Oleaginosas", 588, 25.1, 20.0, 50.0),
                food("Granola", "Cereais", 421, 10.0, 66.0, 13.0),
                food("Mel", "Açúcares", 309, 0.0, 84.0, 0.0),
                food("Chocolate ao leite", "Doces", 540, 7.2, 59.6, 30.3),
                food("Biscoito recheado", "Doces", 472, 6.4, 70.5, 19.6),
                food("Pão de queijo", "Pães", 363, 5.1, 34.2, 24.6),
                food("Pizza de muçarela", "Preparações", 280, 11.0, 30.0, 12.5),
                food("Hambúrguer bovino", "Carnes", 230, 17.0, 4.0, 16.0),
                food("Refrigerante de cola", "Bebidas", 42, 0.0, 10.8, 0.0),
                food("Suco de laranja natural", "Bebidas", 45, 0.7, 10.4, 0.1),
                food("Café sem açúcar", "Bebidas", 2, 0.1, 0.0, 0.0),
                food("Whey protein", "Suplementos", 380, 78.0, 8.0, 5.0)
        ));

        profileRepository.save(new Profile(1L, "Marina Souza", Goal.LOSE_WEIGHT, 1800));

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        meal(yesterday, MealType.BREAKFAST, "Pão francês", 50);
        meal(yesterday, MealType.BREAKFAST, "Café sem açúcar", 100);
        meal(yesterday, MealType.LUNCH, "Arroz branco cozido", 150);
        meal(yesterday, MealType.LUNCH, "Feijão carioca cozido", 100);
        meal(yesterday, MealType.LUNCH, "Alcatra grelhada", 120);
        meal(yesterday, MealType.DINNER, "Pizza de muçarela", 250);

        meal(today, MealType.BREAKFAST, "Ovo cozido", 100);
        meal(today, MealType.BREAKFAST, "Pão de forma integral", 50);
        meal(today, MealType.BREAKFAST, "Mamão papaia", 150);
        meal(today, MealType.LUNCH, "Arroz integral cozido", 120);
        meal(today, MealType.LUNCH, "Feijão preto cozido", 100);
        meal(today, MealType.LUNCH, "Peito de frango grelhado", 120);
        meal(today, MealType.LUNCH, "Alface", 40);
    }

    private static Food food(String name, String category, double calories, double protein, double carbs, double fat) {
        return Food.builder().name(name).category(category)
                .calories(calories).protein(protein).carbs(carbs).fat(fat).build();
    }

    private void meal(LocalDate date, MealType type, String foodName, double grams) {
        Food food = foodRepository.findFirstByNameContainingIgnoreCaseOrderByName(foodName).orElseThrow();
        mealRepository.save(MealEntry.builder().date(date).mealType(type).food(food).grams(grams).build());
    }
}
