package com.example.frontend;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    @FXML private TextField codeField;
    @FXML private TextArea descriptionArea;
    @FXML private Button btnSave, btnCancel;

    @FXML
    public void initialize() {
        btnSave.setOnAction(e -> saveClass());
        btnCancel.setOnAction(e -> cancelCreate());
    }

    private void saveClass() {
        Map<String, String> newClass = new HashMap<>();
        newClass.put("name", nameField.getText());
        newClass.put("code", codeField.getText());
        newClass.put("description", descriptionArea.getText());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(newClass);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.CREATE_CLASS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201) {
                    throw new RuntimeException("Lỗi khi tạo lớp: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Tạo lớp thành công!");
            backToClassPage();
        });
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void cancelCreate() {
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
