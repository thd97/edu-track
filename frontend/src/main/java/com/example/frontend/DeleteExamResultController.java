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

public class DeleteExamResultController {

    @FXML private Label confirmationLabel;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private ExamResultModel examResultToDelete;

    public void setExamResult(ExamResultModel examResult) {
        this.examResultToDelete = examResult;
        confirmationLabel.setText("Bạn có chắc muốn xóa kết quả kỳ thi của học sinh: " + examResult.getStudentName() + "?");
    }

    @FXML
    private void deleteExamResult() {
        if (examResultToDelete == null) {
            showAlert("Không có kết quả kỳ thi nào để xóa.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.DELETE_EXAM_RESULT_API.replace(":id", examResultToDelete.getId())))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa kết quả kỳ thi: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertAndReload("Xóa kết quả kỳ thi thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelDelete() {
        reloadExamResultPage();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndReload(String message) {
        showAlert(message);
        reloadExamResultPage();
    }

    private void reloadExamResultPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/examResult.fxml"));
            Parent examResultRoot = loader.load();
            AnchorPane rootPane = (AnchorPane) confirmationLabel.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(examResultRoot);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
