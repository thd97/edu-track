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

public class UpdateClassController {

    @FXML private TextField nameField;
    @FXML private TextField codeField;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnSave, btnCancel;

    private String classId;

    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> updateClass());
        btnCancel.setOnAction(e -> cancelUpdate());
    }

    public void setClassModel(ClassModel model) {
        this.classId = model.getId();
        nameField.setText(model.getName());
        codeField.setText(model.getCode());
        descriptionArea.setText(model.getDescription());
    }

    private void updateClass() {
        Map<String, String> updatedClass = new HashMap<>();
        updatedClass.put("name", nameField.getText());
        updatedClass.put("code", codeField.getText());
        updatedClass.put("description", descriptionArea.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedClass);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.UPDATE_CLASS_API.replace(":id", classId)))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi cập nhật lớp: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Cập nhật lớp thành công!");
            backToClassPage();
        });
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void cancelUpdate() {
        backToClassPage();
    }

    private void backToClassPage() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/com/example/frontend/class.fxml"));
            AnchorPane contentArea = (AnchorPane) nameField.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(pane);
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
