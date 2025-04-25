package com.example.frontend;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account {
    private final StringProperty id;
    private final StringProperty fullName;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty email;
    private final StringProperty role;
    private final StringProperty phoneNumber;
    private final StringProperty gender;

    public Account(String id, String fullName, String username, String password, String email, String role, String phoneNumber, String gender) {
        this.id = new SimpleStringProperty(id);
        this.fullName = new SimpleStringProperty(fullName);
        this.username = new SimpleStringProperty(username);
        this.password = new SimpleStringProperty(password);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.gender = new SimpleStringProperty(gender);
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

    public String getFullname() {
        return fullName.get();
    }

    public void setFullname(String fullname) {
        this.fullName.set(fullname);
    }

    public StringProperty fullnameProperty() {
        return fullName;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getRole() {
        return role.get();
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    public StringProperty roleProperty() {
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public String getGender() {
        return gender.get();
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public StringProperty genderProperty() {
        return gender;
    }
}