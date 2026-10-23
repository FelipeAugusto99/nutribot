package br.com.fiap.nutribot.service;

import br.com.fiap.nutribot.dto.ProfileRequest;
import br.com.fiap.nutribot.model.Profile;
import br.com.fiap.nutribot.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private static final Long PROFILE_ID = 1L;

    private final ProfileRepository repository;

    public Profile get() {
        return repository.findById(PROFILE_ID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    public Profile update(ProfileRequest request) {
        Profile profile = get();
        profile.setName(request.name());
        profile.setGoal(request.goal());
        profile.setDailyCalories(request.dailyCalories());
        return repository.save(profile);
    }
}
