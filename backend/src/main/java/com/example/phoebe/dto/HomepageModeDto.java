package com.example.phoebe.dto;

import com.example.phoebe.model.HomepageMode;

public class HomepageModeDto {
    private HomepageMode mode;

    // Constructors
    public HomepageModeDto() {}

    public HomepageModeDto(HomepageMode mode) {
        this.mode = mode;
    }

    // Getters
    public HomepageMode getMode() { return mode; }

    // Setters
    public void setMode(HomepageMode mode) { this.mode = mode; }
}
