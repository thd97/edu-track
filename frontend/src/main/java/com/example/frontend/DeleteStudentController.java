package com.example.frontend;

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

public class DeleteStudentController {

    @FXML private Label confirmLabel;
    private StudentModel student;

    public void setStudent(StudentModel student) {
        this.student = student;
        confirmLabel.setText("Bạn có chắc muốn xóa học sinh: " + student.getName() + "?");
    }

    @FXML
    private void confirmDelete() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                String url = ApiConstants.DELETE_STUDENT_API.replace(":id", student.getId());

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa học sinh: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Xóa học sinh thành công.");
            returnToStudentList();
        });
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelDelete() {
        returnToStudentList();
    }

    private void returnToStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/student.fxml"));
            Parent root = loader.load();
            AnchorPane contentArea = (AnchorPane) confirmLabel.getScene().lookup("#contentArea");
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
