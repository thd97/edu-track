package com.example.frontend;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class    UpdateStudentController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<ClassItem> classComboBox;

    private StudentModel student;

    public void setStudent(StudentModel student) {
        this.student = student;
        fullNameField.setText(student.getName());
        emailField.setText(student.getEmail());
        phoneNumberField.setText(student.getPhoneNumber());
        addressField.setText(student.getAddress());
        genderComboBox.setValue(student.getGender());
        if (student.getDateOfBirth() != null && !student.getDateOfBirth().isEmpty()) {
            try {
                dateOfBirthPicker.setValue(java.time.LocalDate.parse(student.getDateOfBirth().substring(0, 10)));
            } catch (Exception e) {
                dateOfBirthPicker.setValue(null);
            }
        } else {
            dateOfBirthPicker.setValue(null);
        }
        fetchClassListAndSelect(student.getClassId());
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().addAll("male", "female", "other");
    }

    private void fetchClassListAndSelect(String selectedClassId) {
        Task<java.util.List<ClassItem>> task = new Task<>() {
            @Override
            protected java.util.List<ClassItem> call() throws Exception {
                java.util.List<ClassItem> classList = new java.util.ArrayList<>();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_CLASSES_API + "?limit=100&page=1"))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject json = new JSONObject(response.body());
                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject cls = data.getJSONObject(i);
                    String id = cls.getString("_id");
                    String name = cls.getString("name");
                    classList.add(new ClassItem(id, name));
                }
                return classList;
            }
        };
        task.setOnSucceeded(e -> {
            classComboBox.getItems().setAll(task.getValue());
            if (selectedClassId != null && !selectedClassId.isEmpty()) {
                for (ClassItem item : classComboBox.getItems()) {
                    if (item.getId().equals(selectedClassId)) {
                        classComboBox.setValue(item);
                        break;
                    }
                }
            }
        });
        task.setOnFailed(e -> classComboBox.getItems().clear());
        new Thread(task).start();
    }

    @FXML
    private void updateStudent() {
        Map<String, String> updatedStudent = new HashMap<>();
        updatedStudent.put("fullName", fullNameField.getText());
        updatedStudent.put("email", emailField.getText());
        updatedStudent.put("phoneNumber", phoneNumberField.getText());
        updatedStudent.put("address", addressField.getText());
        updatedStudent.put("gender", genderComboBox.getValue());
        updatedStudent.put("dateOfBirth", dateOfBirthPicker.getValue() != null ? dateOfBirthPicker.getValue().toString() : "");
        ClassItem selectedClass = classComboBox.getValue();
        updatedStudent.put("classId", selectedClass != null ? selectedClass.getId() : "");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedStudent);

                String url = ApiConstants.UPDATE_STUDENT_API.replace(":id", student.getId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Error updating student: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Student updated successfully.");
            returnToStudentList();
        });
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        returnToStudentList();
    }

    private void returnToStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/students.fxml"));
            Parent root = loader.load();
            AnchorPane contentArea = (AnchorPane) fullNameField.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ClassItem {
        private final String id;
        private final String name;
        public ClassItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        @Override
        public String toString() { return name; }
    }
}
