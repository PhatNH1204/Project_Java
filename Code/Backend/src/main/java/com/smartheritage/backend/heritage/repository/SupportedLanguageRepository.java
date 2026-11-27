package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.SupportedLanguage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportedLanguageRepository extends JpaRepository<SupportedLanguage, String> {

    List<SupportedLanguage> findByActiveTrueOrderByCodeAsc();

    Optional<SupportedLanguage> findByCodeIgnoreCaseAndActiveTrue(String code);
}
