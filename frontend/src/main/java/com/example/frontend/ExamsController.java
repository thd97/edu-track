package com.example.frontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

public class ExamsController {

    @FXML private TableView<ExamModel> examTable;
    @FXML private TableColumn<ExamModel, String> idColumn;
    @FXML private TableColumn<ExamModel, String> nameColumn;
    @FXML private TableColumn<ExamModel, String> examDateColumn;
    @FXML private Button btnNew, btnUpdate, btnDelete;
    @FXML private AnchorPane contentArea;

    private final ObservableList<ExamModel> examList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        fetchExams();

        btnNew.setOnAction(e -> loadPage("/com/example/frontend/newExam.fxml"));
        btnUpdate.setOnAction(e -> updateSelectedExam());
        btnDelete.setOnAction(e -> deleteSelectedExam());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        examDateColumn.setCellValueFactory(cellData -> cellData.getValue().examDateProperty());
        examTable.setItems(examList);
    }

    private void fetchExams() {
        Task<List<ExamModel>> task = new Task<>() {
            @Override
            protected List<ExamModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_EXAMS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // System.out.println("API response: " + response.body());
                if (response.statusCode() == 200) {
                    JSONArray jsonArray = new JSONObject(response.body()).getJSONArray("data");
                    List<ExamModel> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list.add(new ExamModel(
                                obj.getString("_id"),
                                obj.getString("name"),
                                obj.getString("examDate")
                        ));
                    }
                    return list;
                } else {
                    throw new RuntimeException("Failed to load exams: " + response.body());
                }
            }
        };

        task.setOnSucceeded(e -> examList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Lỗi tải dữ liệu kỳ thi: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void loadPage(String fxml) {
        try {
            AnchorPane page = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang: " + fxml);
        }
    }

    private void updateSelectedExam() {
        ExamModel selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kỳ thi để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateExam.fxml"));
            AnchorPane page = loader.load();
            UpdateExamController controller = loader.getController();
            controller.setExam(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật.");
        }
    }

    private void deleteSelectedExam() {
        ExamModel selected = examTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kỳ thi để xóa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deleteExam.fxml"));
            AnchorPane page = loader.load();
            DeleteExamController controller = loader.getController();
            controller.setExam(selected);
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
