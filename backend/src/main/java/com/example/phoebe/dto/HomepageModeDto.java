package com.example.phoebe.dto;

import com.example.phoebe.model.HomepageMode;

public class HomepageModeDto {
    private HomepageMode mode;

    public HomepageModeDto() {
    }

    public HomepageModeDto(HomepageMode mode) {
        this.mode = mode;
    }

    public HomepageMode getMode() {
        return mode;
    }

    public void setMode(HomepageMode mode) {
        this.mode = mode;
    }
}
