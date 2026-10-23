package br.com.fiap.nutribot.repository;

import br.com.fiap.nutribot.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
