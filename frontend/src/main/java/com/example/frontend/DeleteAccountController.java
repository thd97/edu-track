package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DeleteAccountController {

    @FXML private Label confirmLabel;
    private Account accountToDelete;

    public void setAccount(Account account) {
        this.accountToDelete = account;
        confirmLabel.setText("Are you sure you want to delete account: " + account.getFullname() + "?");
    }

    @FXML
    private void deleteAccount() {
        if (accountToDelete == null) {
            showAlert("No account selected for deletion.");
            return;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConstants.DELETE_ACCOUNT_API.replace(":id", accountToDelete.getId())))
                    .header("Authorization", "Bearer " + LoginController.userToken)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                showAlert("Account deleted successfully!");
                returnToAccountList();
            } else {
                showAlert("Error deleting account: " + response.body());
            }
        } catch (Exception e) {
            showAlert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void cancelDelete() {
        returnToAccountList();
    }

    private void returnToAccountList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/account.fxml"));
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
