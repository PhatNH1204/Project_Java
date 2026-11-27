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
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "artifact")
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private HeritageSite site;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "location_name", nullable = false, length = 160)
    private String locationName = "";

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Artifact() {
    }

    public Artifact(HeritageSite site, String code, String locationName,
            BigDecimal latitude, BigDecimal longitude) {
        this.site = site;
        this.code = code;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public HeritageSite getSite() { return site; }
    public String getCode() { return code; }
    public String getLocationName() { return locationName; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }

    public void update(HeritageSite site, String code, String locationName,
            BigDecimal latitude, BigDecimal longitude, boolean active) {
        this.site = site;
        this.code = code;
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }
}
