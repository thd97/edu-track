package com.example.frontend;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class NewExamResultController {

    @FXML private ComboBox<String> examComboBox;
    @FXML private ComboBox<String> studentComboBox;
    @FXML private TextField scoreField;

    private final ObservableList<String> exams = FXCollections.observableArrayList();
    private final ObservableList<String> students = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fetchExams();
        fetchStudents();
    }

    private void fetchExams() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_EXAMS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONArray data = new JSONObject(response.body()).getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        String examId = data.getJSONObject(i).getString("id");
                        exams.add(examId);
                    }
                } else {
                    throw new RuntimeException("Lỗi khi tải danh sách kỳ thi");
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> examComboBox.setItems(exams));
        task.setOnFailed(e -> showAlert("Lỗi tải kỳ thi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void fetchStudents() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_STUDENTS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONArray data = new JSONObject(response.body()).getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        String studentId = data.getJSONObject(i).getString("id");
                        students.add(studentId);
                    }
                } else {
                    throw new RuntimeException("Lỗi khi tải danh sách học sinh");
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> studentComboBox.setItems(students));
        task.setOnFailed(e -> showAlert("Lỗi tải học sinh: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void saveExamResult() {
        String examId = examComboBox.getValue();
        String studentId = studentComboBox.getValue();
        String score = scoreField.getText();

        if (examId == null || studentId == null || score.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        Map<String, String> examResult = new HashMap<>();
        examResult.put("exam", examId);
        examResult.put("student", studentId);
        examResult.put("score", score);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                Gson gson = new Gson();
                String json = gson.toJson(examResult);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.CREATE_EXAM_RESULT_API))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 201) {
                    throw new RuntimeException("Lỗi khi tạo kết quả: " + response.body());
                }

                return null;
            }
        };

        task.setOnSucceeded(e -> showAlertWithRedirect("Tạo kết quả kỳ thi thành công."));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void cancelCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("examResult.fxml"));
            Parent root = loader.load();

            AnchorPane rootPane = (AnchorPane) scoreField.getScene().lookup("#contentArea");
            if (rootPane != null) {
                rootPane.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi khi quay lại danh sách kết quả.");
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
