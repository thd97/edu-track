package com.example.frontend;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewClassController {

    @FXML private TextField nameField;
    @FXML private ComboBox<TeacherItem> teacherComboBox;
    @FXML private Button btnSave, btnCancel;

    @FXML
    public void initialize() {
        fetchTeacherList();
        btnCancel.setOnAction(e -> cancelCreate());
    }

    private void fetchTeacherList() {
        Task<java.util.List<TeacherItem>> task = new Task<>() {
            @Override
            protected java.util.List<TeacherItem> call() throws Exception {
                java.util.List<TeacherItem> teacherList = new java.util.ArrayList<>();
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
                    teacherList.add(new TeacherItem(id, name));
                }
                return teacherList;
            }
        };
        task.setOnSucceeded(e -> teacherComboBox.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> teacherComboBox.getItems().clear());
        new Thread(task).start();
    }

    @FXML
    private void saveClass() {
        String name = nameField.getText().trim();
        TeacherItem selectedTeacher = teacherComboBox.getValue();
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
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(ApiConstants.CREATE_CLASS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofString(classJson.toString()))
                        .build();
                java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201) {
                    throw new RuntimeException("Error creating class: " + response.body());
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            showAlert("Class created successfully!");
            backToClassPage();
        });
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelCreate() {
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

    public static class TeacherItem {
        private final String id;
        private final String name;
        public TeacherItem(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() { return id; }
        public String getName() { return name; }
        @Override
        public String toString() { return name; }
    }
}
