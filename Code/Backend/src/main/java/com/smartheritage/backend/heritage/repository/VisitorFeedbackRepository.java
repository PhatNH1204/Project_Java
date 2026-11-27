package com.smartheritage.backend.heritage.repository;

import com.smartheritage.backend.heritage.entity.VisitorFeedback;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorFeedbackRepository extends JpaRepository<VisitorFeedback, Long> {

    Optional<VisitorFeedback> findByUser_IdAndArtifact_Id(Long userId, Long artifactId);

    List<VisitorFeedback> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    List<VisitorFeedback> findByStatusOrderByCreatedAtDesc(String status);

    List<VisitorFeedback> findByArtifact_IdAndStatusOrderByCreatedAtDesc(Long artifactId, String status);

    @org.springframework.data.jpa.repository.Query(
            "select coalesce(avg(f.rating), 0) from VisitorFeedback f where f.status = 'APPROVED'")
    double averageApprovedRating();
}
