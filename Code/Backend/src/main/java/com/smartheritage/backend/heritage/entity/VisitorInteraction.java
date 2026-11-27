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
import java.time.Instant;

@Entity
@Table(name = "visitor_interaction")
public class VisitorInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interaction_id")
    private Long id;

    @Column(name = "event_key", nullable = false, unique = true, length = 36)
    private String eventKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artifact_id", nullable = false)
    private Artifact artifact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beacon_id")
    private BeaconDevice beacon;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "happened_at", nullable = false)
    private Instant happenedAt;

    protected VisitorInteraction() {
    }

    public VisitorInteraction(String eventKey, UserAccount user, Artifact artifact,
            BeaconDevice beacon, String languageCode, int durationSeconds) {
        this.eventKey = eventKey;
        this.user = user;
        this.artifact = artifact;
        this.beacon = beacon;
        this.languageCode = languageCode;
        this.durationSeconds = durationSeconds;
        this.happenedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEventKey() { return eventKey; }
    public UserAccount getUser() { return user; }
    public Artifact getArtifact() { return artifact; }
    public BeaconDevice getBeacon() { return beacon; }
    public String getLanguageCode() { return languageCode; }
    public int getDurationSeconds() { return durationSeconds; }
    public Instant getHappenedAt() { return happenedAt; }
}
