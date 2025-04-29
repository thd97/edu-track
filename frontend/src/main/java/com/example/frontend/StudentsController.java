package com.example.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class StudentsController {

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> choiceBox;
    @FXML private TableView<StudentModel> studentsTable;
    @FXML private TableColumn<StudentModel, String> idColumn;
    @FXML private TableColumn<StudentModel, String> nameColumn;
    @FXML private TableColumn<StudentModel, String> emailColumn;
    @FXML private TableColumn<StudentModel, String> phoneNumberColumn;
    @FXML private TableColumn<StudentModel, String> addressColumn;
    @FXML private TableColumn<StudentModel, String> genderColumn;
    @FXML private TableColumn<StudentModel, String> dateOfBirthColumn;
    @FXML private AnchorPane contentArea;
    @FXML private Button btnNew, btnUpdate, btnDelete;

    private ObservableList<StudentModel> studentsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList("name", "email"));
        setupTable();
        fetchStudents();

        btnNew.setOnAction(e -> handleNewStudent());
        btnUpdate.setOnAction(e -> handleUpdateStudent());
        btnDelete.setOnAction(e -> handleDeleteStudent());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        emailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        phoneNumberColumn.setCellValueFactory(cell -> cell.getValue().phoneNumberProperty());
        addressColumn.setCellValueFactory(cell -> cell.getValue().addressProperty());
        genderColumn.setCellValueFactory(cell -> cell.getValue().genderProperty());
        dateOfBirthColumn.setCellValueFactory(cell -> cell.getValue().dateOfBirthProperty());

        studentsTable.setItems(studentsList);
    }

    private void fetchStudents() {
        Task<List<StudentModel>> task = new Task<>() {
            @Override
            protected List<StudentModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                String url = ApiConstants.GET_STUDENTS_API + "?limit=10&page=1";

                System.out.println("Đang gọi API: " + url);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Lỗi HTTP: " + response.statusCode());
                }

                JSONObject json = new JSONObject(response.body());
                JSONArray dataArray = json.getJSONArray("data");
                List<StudentModel> list = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    StudentModel student = new StudentModel(
                            item.optString("_id"),
                            item.optString("name"),
                            item.optString("email"),
                            item.optString("phoneNumber"),
                            item.optString("address"),
                            item.optString("gender"),
                            item.optString("dateOfBirth")
                    );
                    list.add(student);
                }
                return list;
            }
        };

        task.setOnSucceeded(e -> studentsList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Lỗi tải dữ liệu: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleNewStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/newStudent.fxml"));
            AnchorPane page = loader.load();
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang tạo mới học sinh.");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        StudentModel selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn học sinh để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateStudent.fxml"));
            AnchorPane page = loader.load();
            UpdateStudentController controller = loader.getController();
            controller.setStudent(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật học sinh.");
        }
    }

    @FXML
    private void handleDeleteStudent() {
        StudentModel selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn học sinh để xóa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deleteStudent.fxml"));
            AnchorPane page = loader.load();
            DeleteStudentController controller = loader.getController();
            controller.setStudent(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang xác nhận xóa học sinh.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String field = choiceBox.getValue();

        if (keyword.isEmpty() || field == null) {
            studentsTable.setItems(studentsList);
            return;
        }

        ObservableList<StudentModel> filtered = FXCollections.observableArrayList();
        for (StudentModel student : studentsList) {
            String value = switch (field) {
                case "name" -> student.getName();
                case "email" -> student.getEmail();
                default -> "";
            };
            if (value.toLowerCase().contains(keyword)) {
                filtered.add(student);
            }
        }
        studentsTable.setItems(filtered);
    }
}
