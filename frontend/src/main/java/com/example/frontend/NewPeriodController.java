package com.example.frontend;

import com.google.gson.Gson;
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
import java.util.HashMap;
import java.util.Map;

public class NewPeriodController {

    @FXML private TextField periodNumberField;

    @FXML private ComboBox<String> startHourBox;
    @FXML private ComboBox<String> startMinuteBox;
    @FXML private ComboBox<String> endHourBox;
    @FXML private ComboBox<String> endMinuteBox;

    @FXML
    public void initialize() {
        // Populate hours (00 - 23)
        for (int i = 0; i < 24; i++) {
            String hour = String.format("%02d", i);
            startHourBox.getItems().add(hour);
            endHourBox.getItems().add(hour);
        }

        // Populate minutes (00, 05, ..., 55)
        for (int i = 0; i < 60; i += 5) {
            String minute = String.format("%02d", i);
            startMinuteBox.getItems().add(minute);
            endMinuteBox.getItems().add(minute);
        }

        // Default selections
        startHourBox.getSelectionModel().select("08");
        startMinuteBox.getSelectionModel().select("00");
        endHourBox.getSelectionModel().select("09");
        endMinuteBox.getSelectionModel().select("30");
    }

    @FXML
    private void savePeriod() {
        String periodNumber = periodNumberField.getText() != null ? periodNumberField.getText().trim() : "";

        String startHour = startHourBox.getValue();
        String startMinute = startMinuteBox.getValue();
        String endHour = endHourBox.getValue();
        String endMinute = endMinuteBox.getValue();

        if (periodNumber.isEmpty() || startHour == null || startMinute == null || endHour == null || endMinute == null) {
            showAlert("Vui lòng nhập đầy đủ thông tin kỳ học và thời gian.");
            return;
        }

        String startTime = startHour + ":" + startMinute;
        String endTime = endHour + ":" + endMinute;

        Map<String, String> newPeriod = new HashMap<>();
        newPeriod.put("periodNumber", periodNumber);
        newPeriod.put("startTime", startTime);
        newPeriod.put("endTime", endTime);

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

            AnchorPane rootPane = (AnchorPane) periodNumberField.getScene().lookup("#contentArea");
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
