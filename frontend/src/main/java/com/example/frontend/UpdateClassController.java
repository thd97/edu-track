package com.example.frontend;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import javafx.scene.Parent;
import javafx.application.Platform;

public class UpdateClassController {

    @FXML private TextField nameField;
    @FXML private ComboBox<NewClassController.TeacherItem> teacherComboBox;
    @FXML private Button btnSave, btnCancel;

    private String classId;
    private String selectedTeacherName;

    @FXML
    public void initialize() {
        fetchTeacherListAndSelect();
        btnCancel.setOnAction(e -> cancelUpdate());
    }

    public void setClassModel(ClassModel model) {
        this.classId = model.getId();
        nameField.setText(model.getName());
        this.selectedTeacherName = model.getTeacherName();
        fetchTeacherListAndSelect();
    }

    private void fetchTeacherListAndSelect() {
        Task<java.util.List<NewClassController.TeacherItem>> task = new Task<>() {
            @Override
            protected java.util.List<NewClassController.TeacherItem> call() throws Exception {
                java.util.List<NewClassController.TeacherItem> teacherList = new java.util.ArrayList<>();
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                org.json.JSONObject body = new org.json.JSONObject();
                body.put("role", "teacher");
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(ApiConstants.FILTER_USERS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                org.json.JSONObject json = new org.json.JSONObject(response.body());
                org.json.JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    org.json.JSONObject teacher = data.getJSONObject(i);
                    String id = teacher.getString("_id");
                    String name = teacher.getString("fullName");
                    teacherList.add(new NewClassController.TeacherItem(id, name));
                }
                return teacherList;
            }
        };

        task.setOnSucceeded(e -> {
            teacherComboBox.getItems().setAll(task.getValue());
            if (selectedTeacherName != null && !selectedTeacherName.isEmpty()) {
                for (NewClassController.TeacherItem item : teacherComboBox.getItems()) {
                    if (item.getName().equals(selectedTeacherName)) {
                        Platform.runLater(() -> {
                            teacherComboBox.setValue(item);
                        });
                        break;
                    }
                }
            }
        });

        task.setOnFailed(e -> {
            teacherComboBox.getItems().clear();
            showAlert("Error loading teacher list: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void updateClass() {
        String name = nameField.getText().trim();
        NewClassController.TeacherItem selectedTeacher = teacherComboBox.getValue();
        if (name.isEmpty() || selectedTeacher == null) {
            showAlert("Please enter class name and select a teacher.");
            return;
        }
        org.json.JSONObject classJson = new org.json.JSONObject();
        classJson.put("name", name);
        classJson.put("teacher", selectedTeacher.getId());
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
                String url = ApiConstants.UPDATE_CLASS_API.replace(":id", classId);
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(url))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .PUT(java.net.http.HttpRequest.BodyPublishers.ofString(classJson.toString()))
                        .build();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Error updating class: " + response.body());
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Class updated successfully!");
            backToClassPage();
        });
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        backToClassPage();
    }

    private void backToClassPage() {
        try {
            Parent pane = FXMLLoader.load(getClass().getResource("/com/example/frontend/class.fxml"));
            AnchorPane contentArea = (AnchorPane) nameField.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(pane);
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
}
