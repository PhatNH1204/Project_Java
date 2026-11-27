package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.MediaUploadView;
import com.smartheritage.backend.heritage.service.MediaStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/media")
@PreAuthorize("hasRole('ADMIN')")
public class MediaController {

    private final MediaStorageService storage;

    public MediaController(MediaStorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MediaUploadView>> upload(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(storage.store(file)));
    }
}
