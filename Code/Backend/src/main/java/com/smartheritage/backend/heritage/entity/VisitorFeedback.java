package com.smartheritage.backend.heritage.entity;

import com.smartheritage.backend.identity.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(name = "visitor_feedback", uniqueConstraints = @UniqueConstraint(
        name = "uq_feedback_user_artifact", columnNames = {"user_id", "artifact_id"}))
public class VisitorFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false, length = 2000)
    private String comment = "";

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected VisitorFeedback() {
    }

    public VisitorFeedback(UserAccount user, Artifact artifact, int rating, String comment) {
        this.user = user;
        this.artifact = artifact;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public UserAccount getUser() { return user; }
    public Artifact getArtifact() { return artifact; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void moderate(String status) {
        this.status = status;
    }

    public void updateRatingAndComment(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        this.status = "PENDING";
    }
}
