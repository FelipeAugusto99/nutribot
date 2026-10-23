package br.com.fiap.nutribot.controller;

import br.com.fiap.nutribot.dto.ProfileRequest;
import br.com.fiap.nutribot.model.Profile;
import br.com.fiap.nutribot.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Perfil do paciente (há um único paciente, id 1)
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping
    public Profile get() {
        return service.get();
    }

    @PutMapping
    public Profile update(@RequestBody @Valid ProfileRequest request) {
        return service.update(request);
    }
}
