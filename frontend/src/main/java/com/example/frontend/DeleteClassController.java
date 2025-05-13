package com.example.frontend;

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

public class DeleteClassController {

    @FXML private Label confirmLabel;
    @FXML private Button btnDelete, btnCancel;

    private String classId;
    private String className;

    public void setClassModel(ClassModel model) {
        this.classId = model.getId();
        this.className = model.getName();
        confirmLabel.setText("Are you sure you want to delete class: " + className + "?");
    }

    @FXML
    private void confirmDelete() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = ApiConstants.DELETE_CLASS_API.replace(":id", classId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + LoginController.userToken)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                showAlert("Class deleted successfully!");
                returnToClassList();
            } else {
                showAlert("Error deleting class: " + response.body());
            }
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void cancelDelete() {
        returnToClassList();
    }

    private void returnToClassList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/class.fxml"));
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
        alert.setTitle("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
