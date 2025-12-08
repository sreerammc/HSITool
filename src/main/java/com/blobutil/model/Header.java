package com.blobutil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public class Header {
    @JsonProperty("SystemName")
    private String systemName;
    
    @JsonProperty("StartDate")
    private Instant startDate;
    
    @JsonProperty("EndDate")
    private Instant endDate;

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }
}






