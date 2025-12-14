package com.example.phoebe.entity;

import com.example.phoebe.model.HomepageMode;
import jakarta.persistence.*;

@Entity
@Table(name = "homepage_settings")
public class HomepageSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private HomepageMode mode;

    // Constructors
    public HomepageSettings() {}

    public HomepageSettings(HomepageMode mode) {
        this.mode = mode;
    }

    // Getters
    public Integer getId() { return id; }
    public HomepageMode getMode() { return mode; }

    // Setters
    public void setId(Integer id) { this.id = id; }
    public void setMode(HomepageMode mode) { this.mode = mode; }
}
