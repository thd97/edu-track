package com.example.frontend;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UpdateExamController {

    @FXML private TextField nameField;
    @FXML private DatePicker examDatePicker;

    private ExamModel exam;

    public void setExam(ExamModel exam) {
        this.exam = exam;
        nameField.setText(exam.getName());
        examDatePicker.setValue(LocalDate.parse(exam.getExamDate().substring(0, 10)));
    }

    @FXML
    private void updateExam() {
        Map<String, Object> updatedExam = new HashMap<>();
        updatedExam.put("name", nameField.getText());
        updatedExam.put("examDate", examDatePicker.getValue().toString() + "T00:00:00.000Z");

        String url = ApiConstants.UPDATE_EXAM_API.replace(":id", exam.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedExam);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi cập nhật kỳ thi: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setContentText("Cập nhật kỳ thi thành công.");
            alert.showAndWait();
            loadExamView();
        });

        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        loadExamView();
    }

    private void loadExamView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/exam.fxml"));
            Parent examPage = loader.load();

            AnchorPane content = (AnchorPane) nameField.getScene().lookup("#contentArea");
            if (content != null) {
                content.getChildren().setAll(examPage);
            } else {
                System.out.println("Không tìm thấy contentArea!");
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
}
