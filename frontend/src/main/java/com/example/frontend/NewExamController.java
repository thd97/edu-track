package com.example.frontend;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewExamController {

    @FXML private TextField nameField;
    @FXML private DatePicker examDatePicker;

    @FXML
    public void initialize() {
        // Có thể thêm init logic ở đây nếu cần
    }

    @FXML
    private void saveExam() {
        String name = nameField.getText();
        String examDate = (examDatePicker.getValue() != null) ? examDatePicker.getValue().toString() : "";

        if (name.isEmpty() || examDate.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin kỳ thi.");
            return;
        }

        Map<String, String> newExam = new HashMap<>();
        newExam.put("name", name);
        newExam.put("examDate", examDate);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(newExam);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.CREATE_EXAM_API))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 201) {
                    throw new RuntimeException("Lỗi khi tạo kỳ thi: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertWithRedirect("Tạo kỳ thi thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/exam.fxml"));
            Parent examPage = loader.load();

            AnchorPane content = (AnchorPane) nameField.getScene().lookup("#contentArea");
            if (content != null) {
                content.getChildren().setAll(examPage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể quay về trang exam.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertWithRedirect(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
        loadExamView();
    }

    private void loadExamView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/exam.fxml"));
            Parent examPage = loader.load();

            AnchorPane content = (AnchorPane) nameField.getScene().lookup("#contentArea");
            if (content != null) {
                content.getChildren().setAll(examPage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể quay về trang exam.");
        }
    }
}
