package com.example.phoebe.repository;

import com.example.phoebe.entity.HomepageSettings;
import com.example.phoebe.model.HomepageMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomepageSettingsRepository extends JpaRepository<HomepageSettings, Integer> {
    Optional<HomepageSettings> findByMode(HomepageMode mode);
}
