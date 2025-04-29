package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    public static String userToken;
    public static String userRole;
    public static String userFullName;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        HashMap<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ApiConstants.LOGIN_API))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new org.json.JSONObject(credentials).toString()))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(responseBody -> {
                        System.out.println("API Login Response: " + responseBody);
                        JSONObject responseJson = new JSONObject(responseBody);
                        if (responseJson.getBoolean("success")) {
                            JSONObject data = responseJson.getJSONObject("data");
                            userToken = data.getString("accessToken");
                            userRole = data.getString("role");
                            userFullName = data.optString("fullName", "");

                            System.out.println("Access Token là: " + userToken);
                            System.out.println("User Role là: " + userRole);

                            javafx.application.Platform.runLater(this::loadMainView);
                        } else {
                            javafx.application.Platform.runLater(() -> showAlert("Đăng nhập thất bại!"));
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        javafx.application.Platform.runLater(() -> showAlert("Lỗi kết nối máy chủ."));
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Đăng nhập thất bại.");
        }
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("EduTrack Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể tải giao diện chính.");
        }
    }

    @FXML
    private void handleForgotPassword() {
        showAlert("Chức năng quên mật khẩu chưa được triển khai.");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}