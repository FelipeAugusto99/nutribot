package br.com.fiap.nutribot.service;

import java.util.List;

import br.com.fiap.nutribot.model.Food;
import br.com.fiap.nutribot.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository repository;

    // sem nome, devolve a tabela inteira; com nome, os alimentos que contêm o texto
    public List<Food> search(String name) {
        if (name == null || name.isBlank()) {
            return repository.findAllByOrderByName();
        }
        return repository.findByNameContainingIgnoreCaseOrderByName(name.trim());
    }

    public Food findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found"));
    }

    // o primeiro alimento cujo nome contém o texto (útil quando o nome vem de uma frase)
    public Food findByName(String name) {
        return repository.findFirstByNameContainingIgnoreCaseOrderByName(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Food not found: " + name));
    }
}
