package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardController {

    @FXML
    private Button btnLogout;

    @FXML
    private VBox tableView;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private void initialize() {
        if (LoginController.userToken == null || LoginController.userToken.isEmpty()) {
            showAlert("Lỗi: Bạn chưa đăng nhập hoặc token không hợp lệ.");
            return;
        }
        try {
            getUserData();
        } catch (Exception e) {
            showAlert("Lỗi kết nối đến máy chủ: " + e.getMessage());
        }
    }

    private void getUserData() throws Exception {
        String apiUrl = ApiConstants.BASE_URL + "/api/users/me";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + LoginController.userToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject responseJson = new JSONObject(response.body());
            // Hiện thông tin người dùng hoặc làm gì đó với dữ liệu
            JSONObject data = responseJson.optJSONObject("data");
            if (data != null) {
                System.out.println("Thông tin người dùng: " + data.toString(2));
                // Ở đây bạn có thể cập nhật UI tùy vào dữ liệu
            } else {
                showAlert("Không có dữ liệu người dùng.");
            }
        } else {
            showAlert("Lỗi khi lấy dữ liệu người dùng. Mã trạng thái: " + response.statusCode());
        }
    }

    @FXML
    private void handleLogout() {
        LoginController.userToken = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể chuyển đến màn hình đăng nhập: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
