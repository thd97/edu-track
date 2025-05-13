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

public class PeriodsController {

    @FXML private TableView<PeriodModel> periodTable;
    @FXML private TableColumn<PeriodModel, String> idColumn;
    @FXML private TableColumn<PeriodModel, String> periodNumberColumn;
    @FXML private TableColumn<PeriodModel, String> startTimeColumn;
    @FXML private TableColumn<PeriodModel, String> endTimeColumn;
    @FXML private Button btnNew, btnUpdate, btnDelete;
    @FXML private AnchorPane contentArea;

    private final ObservableList<PeriodModel> periodList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        fetchPeriods();

        btnNew.setOnAction(e -> loadPage("/com/example/frontend/newPeriod.fxml"));
        btnUpdate.setOnAction(e -> updateSelectedPeriod());
        btnDelete.setOnAction(e -> deleteSelectedPeriod());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        periodNumberColumn.setCellValueFactory(cellData -> cellData.getValue().periodNumberProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());
        periodTable.setItems(periodList);
    }

    private void fetchPeriods() {
        Task<List<PeriodModel>> task = new Task<>() {
            @Override
            protected List<PeriodModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_PERIODS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONArray jsonArray = new JSONObject(response.body()).getJSONArray("data");
                    List<PeriodModel> list = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        list.add(new PeriodModel(
                                obj.getString("_id"),
                                String.valueOf(obj.get("periodNumber")),
                                obj.getString("startTime"),
                                obj.getString("endTime")
                        ));

                    }
                    return list;
                } else {
                    throw new RuntimeException("Failed to load periods: " + response.body());
                }
            }
        };

        task.setOnSucceeded(e -> periodList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Lỗi tải dữ liệu kỳ học: " + task.getException().getMessage()));
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

    private void updateSelectedPeriod() {
        PeriodModel selected = periodTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kỳ học để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updatePeriod.fxml"));
            AnchorPane page = loader.load();
            UpdatePeriodController controller = loader.getController();
            controller.setPeriod(selected);
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật.");
        }
    }

    private void deleteSelectedPeriod() {
        PeriodModel selected = periodTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn kỳ học để xóa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deletePeriod.fxml"));
            AnchorPane page = loader.load();
            DeletePeriodController controller = loader.getController();
            controller.setPeriod(selected);
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
