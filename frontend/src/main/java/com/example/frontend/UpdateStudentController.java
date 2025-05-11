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

public class    UpdateStudentController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> genderChoiceBox;
    @FXML private TextField dateOfBirthField;

    private StudentModel student;

    public void setStudent(StudentModel student) {
        this.student = student;
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        phoneNumberField.setText(student.getPhoneNumber());
        addressField.setText(student.getAddress());
        genderChoiceBox.setValue(student.getGender());
        dateOfBirthField.setText(student.getDateOfBirth());
    }

    @FXML
    public void initialize() {
        genderChoiceBox.getItems().addAll("male", "female", "other");
    }

    @FXML
    private void updateStudent() {
        Map<String, String> updatedStudent = new HashMap<>();
        updatedStudent.put("name", nameField.getText());
        updatedStudent.put("email", emailField.getText());
        updatedStudent.put("phoneNumber", phoneNumberField.getText());
        updatedStudent.put("address", addressField.getText());
        updatedStudent.put("gender", genderChoiceBox.getValue());
        updatedStudent.put("dateOfBirth", dateOfBirthField.getText());

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
                    throw new RuntimeException("Lỗi khi cập nhật học sinh: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Cập nhật học sinh thành công.");
            returnToStudentList();
        });
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        returnToStudentList();
    }

    private void returnToStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/student.fxml"));
            Parent root = loader.load();
            AnchorPane contentArea = (AnchorPane) nameField.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
