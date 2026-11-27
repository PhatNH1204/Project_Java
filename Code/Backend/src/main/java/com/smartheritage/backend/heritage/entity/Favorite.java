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
@Table(name = "favorite", uniqueConstraints = @UniqueConstraint(
        name = "uq_favorite_user_artifact", columnNames = {"user_id", "artifact_id"}))
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Favorite() {
    }

    public Favorite(UserAccount user, Artifact artifact) {
        this.user = user;
        this.artifact = artifact;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public UserAccount getUser() { return user; }
    public Artifact getArtifact() { return artifact; }
    public Instant getCreatedAt() { return createdAt; }
}
