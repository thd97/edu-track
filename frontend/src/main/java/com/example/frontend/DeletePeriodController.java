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

public class DeletePeriodController {

    @FXML private Label confirmationLabel;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private PeriodModel periodToDelete;

    // Set the period to delete and show confirmation message
    public void setPeriod(PeriodModel period) {
        this.periodToDelete = period;
        confirmationLabel.setText("Bạn có chắc muốn xóa");
    }

    @FXML
    private void deletePeriod() {
        if (periodToDelete == null) {
            showAlert("Không có kỳ học nào để xóa.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.DELETE_PERIOD_API.replace(":id", periodToDelete.getId())))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa kỳ học: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertAndReload("Xóa kỳ học thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelDelete() {
        reloadPeriodPage();
    }

    // Show an alert with the provided message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show the alert and reload the period page
    private void showAlertAndReload(String message) {
        showAlert(message);
        reloadPeriodPage();
    }

    // Reload the period page
    private void reloadPeriodPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/period.fxml"));
            Parent periodRoot = loader.load();
            AnchorPane rootPane = (AnchorPane) confirmationLabel.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(periodRoot);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
