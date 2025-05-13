package com.example.frontend;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteExamController {

    @FXML private Label confirmationLabel;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private ExamModel examToDelete;

    public void setExam(ExamModel exam) {
        this.examToDelete = exam;
        confirmationLabel.setText("Bạn có chắc muốn xóa kỳ thi: " + exam.getName() + "?");
    }

    @FXML
    private void deleteExam() {
        if (examToDelete == null) {
            showAlert("Không có kỳ thi nào để xóa.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.DELETE_EXAM_API.replace(":id", examToDelete.getId())))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa kỳ thi: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertAndReload("Xóa kỳ thi thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelDelete() {
        reloadExamPage();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndReload(String message) {
        showAlert(message);
        reloadExamPage();
    }

    private void reloadExamPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/exam.fxml"));
            Parent examRoot = loader.load();
            AnchorPane rootPane = (AnchorPane) confirmationLabel.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(examRoot);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
