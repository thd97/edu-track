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

public class UpdateAccountController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> roleChoiceBox;
    @FXML private TextField phoneNumberField;
    @FXML private ComboBox<String> genderChoiceBox;

    private Account account;

    public void setAccount(Account account) {
        this.account = account;
        fullNameField.setText(account.getFullname());
        usernameField.setText(account.getUsername());
        passwordField.setText(account.getPassword());
        emailField.setText(account.getEmail());
        roleChoiceBox.setValue(account.getRole());
        phoneNumberField.setText(account.getPhoneNumber());
        genderChoiceBox.setValue(account.getGender());
    }

    @FXML
    public void initialize() {
        roleChoiceBox.getItems().addAll("admin", "teacher");
        genderChoiceBox.getItems().addAll("male", "female", "other");
    }

    @FXML
    private void updateAccount() {
        Map<String, String> updatedAccount = new HashMap<>();
        updatedAccount.put("fullName", fullNameField.getText());
        updatedAccount.put("username", usernameField.getText());
        updatedAccount.put("password", passwordField.getText());
        updatedAccount.put("email", emailField.getText());
        updatedAccount.put("role", roleChoiceBox.getValue());
        updatedAccount.put("phoneNumber", phoneNumberField.getText());
        updatedAccount.put("gender", genderChoiceBox.getValue());

        String url = ApiConstants.UPDATE_ACCOUNT_API.replace(":id", account.getId());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(updatedAccount);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setContentText("Cập nhật tài khoản thành công.");
            alert.showAndWait();

            loadAccountView();
        });

        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelUpdate() {
        loadAccountView();
    }

    private void loadAccountView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/account.fxml"));
            Parent accountPage = loader.load();

            AnchorPane content = (AnchorPane) fullNameField.getScene().lookup("#contentArea");
            if (content != null) {
                content.getChildren().setAll(accountPage);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể quay về trang account.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
