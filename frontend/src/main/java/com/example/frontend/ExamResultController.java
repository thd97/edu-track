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

public class ExamResultController {

    @FXML private TableView<ExamResultModel> examResultTable;
    @FXML private TableColumn<ExamResultModel, String> studentNameColumn;
    @FXML private TableColumn<ExamResultModel, String> examNameColumn;
    @FXML private TableColumn<ExamResultModel, String> scoreColumn;
    @FXML private ComboBox<String> filterComboBox;


    @FXML private TextField searchField;
    @FXML private Button btnNew, btnUpdate, btnDelete;
    @FXML private AnchorPane contentArea;

    private final ObservableList<ExamResultModel> resultList = FXCollections.observableArrayList();
    private ObservableList<ExamResultModel> filteredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        fetchExamResults();

        filterComboBox.getItems().addAll("Student Name", "Exam Name", "Score");
        filterComboBox.setValue("Student Name");

        btnNew.setOnAction(e -> loadPage("/com/example/frontend/newExamResult.fxml"));
        btnUpdate.setOnAction(e -> updateSelectedResult());
        btnDelete.setOnAction(e -> deleteSelectedResult());
    }


    private void setupTable() {
        studentNameColumn.setCellValueFactory(cellData -> cellData.getValue().studentNameProperty());
        examNameColumn.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        examResultTable.setItems(filteredList);
    }

    private void fetchExamResults() {
        Task<List<ExamResultModel>> task = new Task<>() {
            @Override
            protected List<ExamResultModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_EXAM_RESULTS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200)
                    throw new RuntimeException("Lỗi tải kết quả thi: " + response.body());

                JSONObject json = new JSONObject(response.body());
                if (!json.has("data"))
                    throw new RuntimeException("Phản hồi không có 'data'");

                JSONArray dataArray = json.getJSONArray("data");
                List<ExamResultModel> list = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject obj = dataArray.getJSONObject(i);
                    JSONObject student = obj.getJSONObject("student");
                    JSONObject exam = obj.getJSONObject("exam");

                    list.add(new ExamResultModel(
                            obj.getString("_id"),
                            exam.getString("_id"),
                            student.getString("_id"),
                            String.valueOf(obj.getInt("score")),
                            student.getString("fullName"),
                            exam.getString("name")
                    ));
                }

                return list;
            }
        };

        task.setOnSucceeded(e -> {
            resultList.setAll(task.getValue());
            filteredList.setAll(resultList);  // Gán ban đầu
        });
        task.setOnFailed(e -> showAlert("Lỗi tải dữ liệu: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        String selectedField = filterComboBox.getValue();

        if (keyword.isEmpty()) {
            filteredList.setAll(resultList);
            return;
        }

        filteredList.setAll(resultList.filtered(result -> {
            switch (selectedField) {
                case "Student Name":
                    return result.getStudentName().toLowerCase().contains(keyword);
                case "Exam Name":
                    return result.getClassName().toLowerCase().contains(keyword);
                case "Score":
                    return result.getScore().toLowerCase().contains(keyword);
                default:
                    return false;
            }
        }));
    }


    private void loadPage(String fxml) {
        try {
            AnchorPane page = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang: " + fxml);
        }
    }

    private void updateSelectedResult() {
        ExamResultModel selected = examResultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kết quả để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateExamResult.fxml"));
            AnchorPane page = loader.load();
            UpdateExamResultController controller = loader.getController();
            controller.setExamResult(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật.");
        }
    }

    private void deleteSelectedResult() {
        ExamResultModel selected = examResultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kết quả để xóa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deleteExamResult.fxml"));
            AnchorPane page = loader.load();
            DeleteExamResultController controller = loader.getController();
            controller.setExamResult(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang xác nhận xóa.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
