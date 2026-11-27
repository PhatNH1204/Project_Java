package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.ArtifactContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtifactContentRepository extends JpaRepository<ArtifactContent, Long> {

    Optional<ArtifactContent> findByArtifact_IdAndLanguageCodeIgnoreCase(Long artifactId, String languageCode);

    List<ArtifactContent> findByArtifact_IdOrderByLanguageCodeAsc(Long artifactId);

    void deleteByArtifact_Id(Long artifactId);
}
