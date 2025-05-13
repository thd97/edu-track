package com.example.frontend;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewAccountController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleChoiceBox;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<String> genderChoiceBox;

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll("admin", "teacher");
        genderChoiceBox.getItems().addAll("male", "female", "other");
    }

    @FXML
    private void saveAccount() {
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String role = roleChoiceBox.getValue();
        String phoneNumber = phoneNumberField.getText();
        String gender = genderChoiceBox.getValue();

        Map<String, String> newAccount = new HashMap<>();
        newAccount.put("fullName", fullName);
        newAccount.put("username", username);
        newAccount.put("password", password);
        newAccount.put("email", email);
        newAccount.put("role", role);
        newAccount.put("phoneNumber", phoneNumber);
        newAccount.put("gender", gender);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(newAccount);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.CREATE_ACCOUNT_API))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 201) {
                    throw new RuntimeException("Error creating account: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertWithRedirect("Account created successfully."));
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
            Parent accountRoot = loader.load();

            AnchorPane rootPane = (AnchorPane) fullNameField.getScene().lookup("#contentArea");
                rootPane.getChildren().setAll(accountRoot);
            

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error canceling account creation.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertWithRedirect(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText("Message");
        alert.setContentText(message);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("account.fxml"));
                    Parent accountRoot = loader.load();
                    AnchorPane rootPane = (AnchorPane) fullNameField.getScene().lookup("#contentArea");
                        rootPane.getChildren().setAll(accountRoot);
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error returning to account list.");
                }
            }
        });
    }
}
