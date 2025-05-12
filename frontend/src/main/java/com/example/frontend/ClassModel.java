package com.example.frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClassModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty code;
    private final StringProperty description;
    private final StringProperty createdAt;
    private final StringProperty updatedAt;
    private final StringProperty teacherName;
    private final StringProperty totalStudent;

    public ClassModel(String id, String name, String code, String description, String createdAt, String updatedAt, String teacherName, String totalStudent) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.code = new SimpleStringProperty(code);
        this.description = new SimpleStringProperty(description);
        this.createdAt = new SimpleStringProperty(createdAt);
        this.updatedAt = new SimpleStringProperty(updatedAt);
        this.teacherName = new SimpleStringProperty(teacherName);
        this.totalStudent = new SimpleStringProperty(totalStudent);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public StringProperty codeProperty() {
        return code;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt.get();
    }

    public void setCreateAt(String createat) {
        this.createdAt.set(createat);
    }

    public StringProperty createdAtProperty() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt.get();
    }

    public void setUpdateAt(String updateat) {
        this.updatedAt.set(updateat);
    }

    public StringProperty updatedAtProperty() {
        return updatedAt;
    }

    public String getTeacherName() {
        return teacherName.get();
    }

    public void setTeacherName(String teacherName) {
        this.teacherName.set(teacherName);
    }

    public StringProperty teacherNameProperty() {
        return teacherName;
    }

    public String getTotalStudent() {
        return totalStudent.get();
    }

    public void setTotalStudent(String totalStudent) {
        this.totalStudent.set(totalStudent);
    }

    public StringProperty totalStudentProperty() {
        return totalStudent;
    }
}
