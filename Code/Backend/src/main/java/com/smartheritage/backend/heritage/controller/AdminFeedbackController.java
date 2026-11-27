package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.FeedbackModerationRequest;
import com.smartheritage.backend.heritage.dto.FeedbackView;
import com.smartheritage.backend.heritage.service.AdminFeedbackService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/feedback")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFeedbackController {

    private final AdminFeedbackService feedback;

    public AdminFeedbackController(AdminFeedbackService feedback) {
        this.feedback = feedback;
    }

    @GetMapping
    public ApiResponse<List<FeedbackView>> feedback(
            @RequestParam(required = false) String status) {
        return ApiResponse.of(feedback.list(status));
    }

    @PatchMapping("/{id}")
    public ApiResponse<FeedbackView> moderate(
            @PathVariable Long id, @Valid @RequestBody FeedbackModerationRequest request) {
        return ApiResponse.of(feedback.moderate(id, request.status()));
    }
}
