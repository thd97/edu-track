package com.example.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class NewStudentController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<ClassItem> classComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final ObservableList<ClassItem> classList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        genderCombo.setItems(FXCollections.observableArrayList("male", "female", "other"));
        fetchClassList();

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    private void fetchClassList() {
        try {
            URL url = new URL("http://localhost:3004/api/classes?limit=100&page=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + LoginController.userToken);

            Scanner scanner = new Scanner(conn.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject json = new JSONObject(response);
            JSONArray data = json.getJSONArray("data");

            for (int i = 0; i < data.length(); i++) {
                JSONObject cls = data.getJSONObject(i);
                String id = cls.getString("_id");
                String name = cls.getString("name");
                classList.add(new ClassItem(id, name));
            }

            classComboBox.setItems(classList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi khi tải danh sách lớp học.");
        }
    }

    private void handleSave() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String gender = genderCombo.getValue();
            String dob = dobPicker.getValue() != null
                    ? dobPicker.getValue().format(DateTimeFormatter.ISO_DATE)
                    : "";
            String address = addressField.getText().trim();

            ClassItem selectedClass = classComboBox.getValue();
            if (selectedClass == null) {
                showAlert("Vui lòng chọn lớp học.");
                return;
            }
            String classId = selectedClass.getId();

            JSONObject studentJson = new JSONObject();
            studentJson.put("fullName", name);
            studentJson.put("email", email);
            studentJson.put("phoneNumber", phone);
            studentJson.put("gender", gender);
            studentJson.put("dateOfBirth", dob);
            studentJson.put("address", address);
            studentJson.put("class", classId);

            URL url = new URL("http://localhost:3004/api/students");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + LoginController.userToken);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = studentJson.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            Scanner scanner;
            if (responseCode >= 200 && responseCode < 300) {
                scanner = new Scanner(conn.getInputStream());
                showAlert("Tạo học sinh thành công.");
                loadStudentList();
            } else {
                scanner = new Scanner(conn.getErrorStream());
                String errorResponse = scanner.useDelimiter("\\A").next();
                showAlert("Lỗi khi tạo học sinh: " + errorResponse);
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi không xác định: " + e.getMessage());
        }
    }

    private void handleCancel() {
        loadStudentList();
    }

    private void loadStudentList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("students.fxml"));
            Parent root = loader.load();
            AnchorPane contentArea = (AnchorPane) nameField.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.getChildren().setAll(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Không thể quay lại danh sách học sinh.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Class nội bộ đại diện cho mỗi lớp học
    public static class ClassItem {
        private final String id;
        private final String name;

        public ClassItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }
}
