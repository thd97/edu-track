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

public class DeleteAccountController {

    @FXML private Label confirmationLabel;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private Account accountToDelete;

    public void setAccount(Account account) {
        this.accountToDelete = account;
        confirmationLabel.setText("Bạn có chắc muốn xóa tài khoản của " + account.getFullname() + "?");
    }

    @FXML
    private void deleteAccount() {
        if (accountToDelete == null) {
            showAlert("Không có tài khoản nào để xóa.");
            return;
        }

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.DELETE_ACCOUNT_API.replace(":id", accountToDelete.getId())))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .DELETE()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi khi xóa tài khoản: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertAndReload("Xóa tài khoản thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void cancelDelete() {
        reloadAccountsPage();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndReload(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
        reloadAccountsPage();
    }

    private void reloadAccountsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/account.fxml"));
            Parent accountRoot = loader.load();
            AnchorPane rootPane = (AnchorPane) confirmationLabel.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(accountRoot);
            } else {
                System.out.println("Không tìm thấy contentArea!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
