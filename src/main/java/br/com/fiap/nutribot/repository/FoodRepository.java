package br.com.fiap.nutribot.repository;

import java.util.List;
import java.util.Optional;

import br.com.fiap.nutribot.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {

    List<Food> findByNameContainingIgnoreCaseOrderByName(String name);

    List<Food> findAllByOrderByName();

    Optional<Food> findFirstByNameContainingIgnoreCaseOrderByName(String name);
}
