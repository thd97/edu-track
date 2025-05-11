package com.example.frontend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StudentModel {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private final StringProperty gender;
    private final StringProperty dateOfBirth;
    private final StringProperty classId;
    private final StringProperty className;

    public StudentModel(String id, String name, String email, String phoneNumber,
                        String address, String gender, String dateOfBirth,
                        String classId, String className) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.gender = new SimpleStringProperty(gender);
        this.dateOfBirth = new SimpleStringProperty(dateOfBirth);
        this.classId = new SimpleStringProperty(classId);
        this.className = new SimpleStringProperty(className);
    }

    public String getId() { return id.get(); }
    public StringProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getAddress() { return address.get(); }
    public StringProperty addressProperty() { return address; }

    public String getGender() { return gender.get(); }
    public StringProperty genderProperty() { return gender; }

    public String getDateOfBirth() { return dateOfBirth.get(); }
    public StringProperty dateOfBirthProperty() { return dateOfBirth; }

    public String getClassId() { return classId.get(); }
    public StringProperty classIdProperty() { return classId; }

    public String getClassName() { return className.get(); }
    public StringProperty classNameProperty() { return className; }
}
