package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.common.NotFoundException;
import com.smartheritage.backend.heritage.dto.FeedbackView;
import com.smartheritage.backend.heritage.repository.VisitorFeedbackRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminFeedbackService {

    private final VisitorFeedbackRepository feedback;

    public AdminFeedbackService(VisitorFeedbackRepository feedback) {
        this.feedback = feedback;
    }

    @Transactional(readOnly = true)
    public List<FeedbackView> list(String status) {
        List<com.smartheritage.backend.heritage.entity.VisitorFeedback> items;
        if (status == null || status.isBlank()) {
            items = feedback.findAllByOrderByCreatedAtDesc();
        } else {
            String normalized = status.trim().toUpperCase(Locale.ROOT);
            validateStatus(normalized);
            items = feedback.findByStatusOrderByCreatedAtDesc(normalized);
        }
        return items.stream().map(this::toView).toList();
    }

    @Transactional
    public FeedbackView moderate(Long id, String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        validateStatus(normalized);
        var item = feedback.findById(id)
                .orElseThrow(() -> new NotFoundException("Feedback not found"));
        item.moderate(normalized);
        return toView(item);
    }

    private void validateStatus(String status) {
        if (!status.equals("APPROVED") && !status.equals("REJECTED")) {
            throw new BadRequestException("Feedback status must be APPROVED or REJECTED");
        }
    }

    private FeedbackView toView(com.smartheritage.backend.heritage.entity.VisitorFeedback item) {
        return new FeedbackView(item.getId(), item.getArtifact().getId(),
                item.getArtifact().getCode(), item.getUser().getId(),
                item.getUser().getUsername(), item.getRating(), item.getComment(),
                item.getStatus(), item.getCreatedAt());
    }
}
