package com.example.frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExamModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty examDate;

    public ExamModel(String id, String name, String examDate) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.examDate = new SimpleStringProperty(examDate);
    }

    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getExamDate() {
        return examDate.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty examDateProperty() {
        return examDate;
    }
}
