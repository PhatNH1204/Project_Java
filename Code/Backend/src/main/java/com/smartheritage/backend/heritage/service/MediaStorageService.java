package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.common.BadRequestException;
import com.smartheritage.backend.heritage.dto.MediaUploadView;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaStorageService {

    private static final long MAX_SIZE = 20L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "video/mp4", ".mp4",
            "video/webm", ".webm",
            "audio/mpeg", ".mp3",
            "audio/ogg", ".ogg",
            "audio/wav", ".wav",
            "audio/x-wav", ".wav");

    private final Path directory;

    public MediaStorageService(@Value("${app.upload-dir}") String directory) {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    public MediaUploadView store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BadRequestException("Media files must not exceed 20 MB");
        }
        String contentType = file.getContentType();
        if (contentType != null) {
            contentType = contentType.split(";", 2)[0].trim().toLowerCase(java.util.Locale.ROOT);
        }
        String extension = contentType == null ? null : EXTENSIONS.get(contentType);
        if (extension == null) {
            throw new BadRequestException("Only JPEG, PNG, WebP, MP4, WebM, MP3, OGG, and WAV files are allowed");
        }
        try (var input = file.getInputStream()) {
            if (!hasMatchingSignature(contentType, input.readNBytes(12))) {
                throw new BadRequestException("Uploaded file content does not match its media type");
            }
        } catch (IOException exception) {
            throw new MediaStorageException("Unable to read uploaded media", exception);
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = directory.resolve(filename).normalize();
        if (!destination.getParent().equals(directory)) {
            throw new BadRequestException("Invalid media filename");
        }
        try {
            Files.createDirectories(directory);
            try (var input = file.getInputStream()) {
                Files.copy(input, destination);
            }
        } catch (IOException exception) {
            throw new MediaStorageException("Unable to store uploaded media", exception);
        }
        return new MediaUploadView("/media/" + filename, contentType, file.getSize());
    }

    public String location() {
        String location = directory.toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }

    private boolean hasMatchingSignature(String contentType, byte[] header) {
        return switch (contentType) {
            case "image/jpeg" -> startsWith(header, 0xff, 0xd8, 0xff);
            case "image/png" -> startsWith(header, 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a);
            case "image/webp" -> startsWithAscii(header, 0, "RIFF")
                    && startsWithAscii(header, 8, "WEBP");
            case "video/mp4" -> startsWithAscii(header, 4, "ftyp");
            case "video/webm" -> startsWith(header, 0x1a, 0x45, 0xdf, 0xa3);
            case "audio/ogg" -> startsWithAscii(header, 0, "OggS");
            case "audio/wav", "audio/x-wav" -> startsWithAscii(header, 0, "RIFF")
                    && startsWithAscii(header, 8, "WAVE");
            case "audio/mpeg" -> startsWithAscii(header, 0, "ID3")
                    || startsWith(header, 0xff, 0xfb) || startsWith(header, 0xff, 0xf3)
                    || startsWith(header, 0xff, 0xf2);
            default -> false;
        };
    }

    private boolean startsWith(byte[] value, int... prefix) {
        if (value.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if ((value[i] & 0xff) != prefix[i]) return false;
        }
        return true;
    }

    private boolean startsWithAscii(byte[] value, int offset, String prefix) {
        byte[] expected = prefix.getBytes(StandardCharsets.US_ASCII);
        if (offset + expected.length > value.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if (value[offset + i] != expected[i]) return false;
        }
        return true;
    }
}
