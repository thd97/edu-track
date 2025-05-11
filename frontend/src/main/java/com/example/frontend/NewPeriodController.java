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

public class NewPeriodController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML
    public void initialize() {
        // Khởi tạo nếu cần
    }

    @FXML
    private void savePeriod() {
        String startDate = (startDatePicker.getValue() != null) ? startDatePicker.getValue().toString() : "";
        String endDate = (endDatePicker.getValue() != null) ? endDatePicker.getValue().toString() : "";

        if (startDate.isEmpty() || endDate.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin kỳ học.");
            return;
        }

        Map<String, String> newPeriod = new HashMap<>();
        newPeriod.put("startDate", startDate);
        newPeriod.put("endDate", endDate);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(newPeriod);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.CREATE_PERIOD_API))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201) {
                    throw new RuntimeException("Lỗi khi tạo kỳ học: " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertWithRedirect("Tạo kỳ học thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("period.fxml"));
            Parent periodRoot = loader.load();

            AnchorPane rootPane = (AnchorPane) startDatePicker.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(periodRoot);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi hủy tạo kỳ học.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertWithRedirect(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText("Message");
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cancelCreate();
            }
        });
    }
}
