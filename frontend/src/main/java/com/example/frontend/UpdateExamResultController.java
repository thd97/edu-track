package com.example.frontend;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class UpdateExamResultController {

    @FXML private TextField studentIdField;
    @FXML private TextField examIdField;
    @FXML private TextField scoreField;

    private ExamResultModel examResult;

    public void setExamResult(ExamResultModel examResult) {
        this.examResult = examResult;
        studentIdField.setText(examResult.getStudentId());
        examIdField.setText(examResult.getExamId());
        scoreField.setText(String.valueOf(examResult.getScore()));
    }

    @FXML
    private void updateExamResult() {
        Map<String, Object> updatedResult = new HashMap<>();
        updatedResult.put("studentId", studentIdField.getText());
        updatedResult.put("examId", examIdField.getText());
        updatedResult.put("score", Double.parseDouble(scoreField.getText()));

        String url = ApiConstants.UPDATE_EXAM_RESULT_API.replace(":id", examResult.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedResult);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi cập nhật kết quả: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setContentText("Cập nhật kết quả thành công.");
            alert.showAndWait();
            loadExamResultView();
        });

        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        loadExamResultView();
    }

    private void loadExamResultView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/examResult.fxml"));
            Parent examPage = loader.load();

            AnchorPane content = (AnchorPane) studentIdField.getScene().lookup("#contentArea");
                content.getChildren().setAll(examPage);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể quay về trang exam result.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
