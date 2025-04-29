package com.example.frontend;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteClassController {

    @FXML private Label confirmLabel;
    @FXML private Button btnDelete, btnCancel;

    private String classId;

    public void setClassModel(ClassModel model) {
        this.classId = model.getId();
        confirmLabel.setText("Bạn có chắc muốn xóa lớp " + model.getName() + "?");
    }

    @FXML
    public void initialize() {
        btnDelete.setOnAction(e -> deleteClass());
        btnCancel.setOnAction(e -> cancelDelete());
    }

    private void deleteClass() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.DELETE_CLASS_API.replace(":id", classId)))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa lớp: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showAlert("Xóa lớp thành công!");
            backToClassPage();
        });
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void cancelDelete() {
        backToClassPage();
    }

    private void backToClassPage() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/com/example/frontend/class.fxml"));
            AnchorPane contentArea = (AnchorPane) confirmLabel.getScene().lookup("#contentArea");
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
