package com.smartheritage.backend.heritage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "artifact_content")
public class ArtifactContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 10000)
    private String description;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    @Column(name = "audio_url", length = 1000)
    private String audioUrl;

    protected ArtifactContent() {
    }

    public ArtifactContent(Artifact artifact, String languageCode, String title,
            String description, String imageUrl, String videoUrl, String audioUrl) {
        this.artifact = artifact;
        this.languageCode = languageCode;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
    }

    public Long getId() { return id; }
    public Artifact getArtifact() { return artifact; }
    public String getLanguageCode() { return languageCode; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getVideoUrl() { return videoUrl; }
    public String getAudioUrl() { return audioUrl; }

    public void update(String title, String description, String imageUrl, String videoUrl, String audioUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
    }
}
