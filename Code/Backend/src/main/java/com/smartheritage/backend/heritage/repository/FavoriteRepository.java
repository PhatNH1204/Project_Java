package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByUser_IdAndArtifact_Id(Long userId, Long artifactId);
}
