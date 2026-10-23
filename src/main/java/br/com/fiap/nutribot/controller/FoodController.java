package br.com.fiap.nutribot.controller;

import java.util.List;

import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Tabela nutricional: valores por 100 g
@RestController
@RequestMapping("/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService service;

    @GetMapping
    public List<Food> search(@RequestParam(required = false) String name) {
        return service.search(name);
    }

    @GetMapping("/{id}")
    public Food findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
