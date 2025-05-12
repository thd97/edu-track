package com.example.frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ExamResultModel {
    private final StringProperty id;
    private final StringProperty exam;
    private final StringProperty student;
    private final StringProperty score;
    private final StringProperty studentName;
    private final StringProperty examName;

    public ExamResultModel(String id, String exam, String student, String score, String studentName, String className) {
        this.id = new SimpleStringProperty(id);
        this.exam = new SimpleStringProperty(exam);
        this.student = new SimpleStringProperty(student);
        this.score = new SimpleStringProperty(score);
        this.studentName = new SimpleStringProperty(studentName);
        this.examName = new SimpleStringProperty(className);
    }

    public String getId() {
        return id.get();
    }

    public String getExamId() {
        return exam.get();
    }

    public String getStudentId() {
        return student.get();
    }

    public String getScore() {
        return score.get();
    }

    public String getStudentName() {
        return studentName.get();
    }

    public String getClassName() {
        return examName.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty examProperty() {
        return exam;
    }

    public StringProperty studentProperty() {
        return student;
    }

    public StringProperty scoreProperty() {
        return score;
    }

    public StringProperty studentNameProperty() {
        return studentName;
    }

    public StringProperty classNameProperty() {
        return examName;
    }
}
