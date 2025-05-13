package com.example.frontend;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class UpdatePeriodController {

    @FXML private TextField periodNumberField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    private PeriodModel period;

    public void setPeriod(PeriodModel period) {
        this.period = period;
        periodNumberField.setText(period.getPeriodNumber());
        startTimeField.setText(period.getStartTime());
        endTimeField.setText(period.getEndTime());
    }

    @FXML
    private void updatePeriod() {
        Map<String, Object> updatedPeriod = new HashMap<>();
        updatedPeriod.put("periodNumber", periodNumberField.getText());
        updatedPeriod.put("startTime", startTimeField.getText());
        updatedPeriod.put("endTime", endTimeField.getText());

        String url = ApiConstants.UPDATE_PERIOD_API.replace(":id", period.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedPeriod);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi cập nhật: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setContentText("Cập nhật thành công.");
            alert.showAndWait();
            loadPeriodView();
        });

        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        loadPeriodView();
    }

    private void loadPeriodView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/period.fxml"));
            Parent periodPage = loader.load();

            AnchorPane content = (AnchorPane) periodNumberField.getScene().lookup("#contentArea");
                content.getChildren().setAll(periodPage);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể quay về trang Period.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
