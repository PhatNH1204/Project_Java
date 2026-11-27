package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.Artifact;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtifactRepository extends JpaRepository<Artifact, Long> {

    List<Artifact> findByActiveTrueOrderByCodeAsc();

    List<Artifact> findByActiveTrueAndSite_IdOrderByCodeAsc(Long siteId);

    Optional<Artifact> findByIdAndActiveTrue(Long id);

    List<Artifact> findBySite_IdOrderByCodeAsc(Long siteId);

    @Query("select a from Artifact a where a.active = true "
            + "and (:siteId is null or a.site.id = :siteId) "
            + "and (:search is null or lower(a.code) like lower(concat('%', :search, '%')) "
            + "or lower(a.locationName) like lower(concat('%', :search, '%')))")
    List<Artifact> searchActive(@Param("siteId") Long siteId, @Param("search") String search);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByCodeIgnoreCase(String code);
}
