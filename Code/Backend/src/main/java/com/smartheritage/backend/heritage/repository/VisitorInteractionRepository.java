package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.VisitorInteraction;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitorInteractionRepository extends JpaRepository<VisitorInteraction, Long> {

    List<VisitorInteraction> findByUser_IdOrderByHappenedAtDesc(Long userId);

    boolean existsByUser_IdAndEventKey(Long userId, String eventKey);

    Optional<VisitorInteraction> findByUser_IdAndEventKey(Long userId, String eventKey);

    boolean existsByUser_IdAndBeacon_IdAndHappenedAtAfter(Long userId, Long beaconId, Instant after);

    long countByHappenedAtAfter(Instant after);

    @Query("select count(distinct i.user.id) from VisitorInteraction i where i.happenedAt >= :after")
    long countDistinctVisitorsSince(@Param("after") Instant after);

    @Query("select i.artifact.id, i.artifact.code, count(i.id) from VisitorInteraction i "
            + "where i.happenedAt >= :after group by i.artifact.id, i.artifact.code "
            + "order by count(i.id) desc")
    List<Object[]> findPopularArtifacts(@Param("after") Instant after);

    @Query("select i.languageCode, count(i.id) from VisitorInteraction i "
            + "where i.happenedAt >= :after group by i.languageCode order by count(i.id) desc")
    List<Object[]> countByLanguageSince(@Param("after") Instant after);

    @Query("select coalesce(avg(i.durationSeconds), 0) from VisitorInteraction i where i.happenedAt >= :after")
    double averageDurationSince(@Param("after") Instant after);
}
