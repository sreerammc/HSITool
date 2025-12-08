package com.blobutil.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Objects;

public class DataObject {
    @JsonProperty("Id")
    private Long id;
    
    @JsonProperty("Fullname")
    private String fullname;
    
    @JsonProperty("Time")
    private Instant time;
    
    @JsonProperty("Value")
    private Double value;
    
    @JsonProperty("Reason")
    private Integer reason;
    
    @JsonProperty("State")
    private String state;
    
    @JsonProperty("Units")
    private String units;
    
    @JsonProperty("Quality")
    private String quality;
    
    @JsonProperty("QualityNumeric")
    private Integer qualityNumeric;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Integer getQualityNumeric() {
        return qualityNumeric;
    }

    public void setQualityNumeric(Integer qualityNumeric) {
        this.qualityNumeric = qualityNumeric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataObject that = (DataObject) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(fullname, that.fullname) &&
               Objects.equals(time, that.time) &&
               Objects.equals(value, that.value) &&
               Objects.equals(reason, that.reason) &&
               Objects.equals(state, that.state) &&
               Objects.equals(units, that.units) &&
               Objects.equals(quality, that.quality) &&
               Objects.equals(qualityNumeric, that.qualityNumeric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullname, time, value, reason, state, units, quality, qualityNumeric);
    }

    @Override
    public String toString() {
        return "DataObject{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", time=" + time +
                ", value=" + value +
                ", reason=" + reason +
                ", state='" + state + '\'' +
                ", units='" + units + '\'' +
                ", quality='" + quality + '\'' +
                ", qualityNumeric=" + qualityNumeric +
                '}';
    }
}






