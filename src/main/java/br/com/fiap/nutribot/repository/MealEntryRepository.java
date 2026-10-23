package br.com.fiap.nutribot.repository;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.nutribot.model.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByDateOrderByMealTypeAscIdAsc(LocalDate date);
}
