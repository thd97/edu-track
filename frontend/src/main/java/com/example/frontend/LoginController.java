package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject; // Nhớ import thư viện này (có thể cần thêm vào thư viện ngoài nếu chưa có)

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    // Biến static để lưu token có thể dùng toàn cục
    public static String userToken;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        try {
            int status = sendLoginRequest(username, password);
            if (status == 200) {
                loadMainView();
            } else {
                showAlert("Đăng nhập thất bại. Vui lòng kiểm tra lại tài khoản.");
            }
        } catch (Exception e) {
            showAlert("Lỗi kết nối đến máy chủ: " + e.getMessage());
        }
    }

    private int sendLoginRequest(String username, String password) throws Exception {
        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\"}", username, password);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConstants.LOGIN_API))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject responseBody = new JSONObject(response.body());
            // Giả sử server trả về JSON có key là "token"
            if (responseBody.has("token")) {
                userToken = responseBody.getString("token");
                System.out.println("Token lưu được: " + userToken);
            }
        }

        return response.statusCode();
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert("Không thể tải giao diện chính: " + e.getMessage());
        }
    }

    @FXML
    private void handleForgotPassword() {
        showAlert("Chức năng quên mật khẩu chưa được triển khai.");
    }

    @FXML
    private void handleClose() {
        System.exit(0);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
