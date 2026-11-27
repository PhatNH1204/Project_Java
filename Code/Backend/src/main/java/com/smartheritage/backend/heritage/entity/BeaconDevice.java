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
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "beacon_device", uniqueConstraints = @UniqueConstraint(
        name = "uq_beacon_identity", columnNames = {"beacon_uuid", "major", "minor"}))
public class BeaconDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beacon_id")
    private Long id;

    @Column(name = "beacon_uuid", nullable = false, length = 36)
    private String beaconUuid;

    @Column(nullable = false)
    private int major;

    @Column(nullable = false)
    private int minor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artifact_id")
    private Artifact artifact;

    @Column(nullable = false)
    private String placement = "";

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "tx_power", nullable = false)
    private int txPower = -59;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected BeaconDevice() {
    }

    public BeaconDevice(String beaconUuid, int major, int minor, Artifact artifact,
            String placement, BigDecimal latitude, BigDecimal longitude, int txPower) {
        this.beaconUuid = beaconUuid;
        this.major = major;
        this.minor = minor;
        this.artifact = artifact;
        this.placement = placement;
        this.latitude = latitude;
        this.longitude = longitude;
        this.txPower = txPower;
    }

    public Long getId() { return id; }
    public String getBeaconUuid() { return beaconUuid; }
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public Artifact getArtifact() { return artifact; }
    public String getPlacement() { return placement; }
    public BigDecimal getLatitude() { return latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public int getTxPower() { return txPower; }
    public boolean isActive() { return active; }

    public void update(String beaconUuid, int major, int minor, Artifact artifact, String placement,
            BigDecimal latitude, BigDecimal longitude, int txPower, boolean active) {
        this.beaconUuid = beaconUuid;
        this.major = major;
        this.minor = minor;
        this.artifact = artifact;
        this.placement = placement;
        this.latitude = latitude;
        this.longitude = longitude;
        this.txPower = txPower;
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void assign(Artifact artifact) {
        this.artifact = artifact;
    }
}
