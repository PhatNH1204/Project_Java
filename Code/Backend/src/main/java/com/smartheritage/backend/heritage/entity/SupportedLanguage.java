package com.smartheritage.backend.heritage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "supported_language")
public class SupportedLanguage {

    @Id
    @Column(name = "language_code", length = 10)
    private String code;

    @Column(name = "language_name", nullable = false, length = 80)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected SupportedLanguage() {
    }

    public SupportedLanguage(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isActive() { return active; }

    public void updateName(String name) {
        this.name = name;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
