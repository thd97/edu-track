package com.example.frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PeriodModel {
    private final StringProperty id;
    private final StringProperty periodNumber;
    private final StringProperty startTime;
    private final StringProperty endTime;

    public PeriodModel(String id, String periodNumber, String startTime, String endTime) {
        this.id = new SimpleStringProperty(id);
        this.periodNumber = new SimpleStringProperty(periodNumber);
        this.startTime = new SimpleStringProperty(startTime);
        this.endTime = new SimpleStringProperty(endTime);
    }

    public String getId() {
        return id.get();
    }

    public String getPeriodNumber() {
        return periodNumber.get();
    }

    public String getStartTime() {
        return startTime.get();
    }

    public String getEndTime() {
        return endTime.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty periodNumberProperty() {
        return periodNumber;
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }
}
