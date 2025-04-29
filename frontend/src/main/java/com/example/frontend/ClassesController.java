package com.example.frontend;

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
import java.util.ArrayList;
import java.util.List;

public class ClassesController {

    @FXML
    private TableView<ClassModel> classTable;

    @FXML
    private TableColumn<ClassModel, String> idColumn;

    @FXML
    private TableColumn<ClassModel, String> nameColumn;

    @FXML
    private TableColumn<ClassModel, String> codeColumn;

    @FXML
    private TableColumn<ClassModel, String> descriptionColumn;

    @FXML
    private TableColumn<ClassModel, String> createdAtColumn;

    @FXML
    private TableColumn<ClassModel, String> updatedAtColumn;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private ComboBox<String> choiceBox;

    @FXML private Button btnNew, btnUpdate, btnDelete;

    @FXML
    private TableView<ClassModel> classesTable;

    @FXML
    private TextField searchField;

    private ObservableList<ClassModel> classesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList("name", "code", "description"));
        setupTable();
        fetchClasses();

        btnNew.setOnAction(e -> handleNewClass());
        btnUpdate.setOnAction(e -> handleUpdateClass());
        btnDelete.setOnAction(e -> handleDeleteClass());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        codeColumn.setCellValueFactory(cell -> cell.getValue().codeProperty());
        descriptionColumn.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        createdAtColumn.setCellValueFactory(cell -> cell.getValue().createdAtProperty());
        updatedAtColumn.setCellValueFactory(cell -> cell.getValue().updatedAtProperty());

        classesTable.setItems(classesList);
    }

    private void fetchClasses() {
        Task<List<ClassModel>> task = new Task<>() {
            @Override
            protected List<ClassModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                String url = ApiConstants.GET_CLASSES_API + "?limit=10&page=1";

                System.out.println("Đang gọi API: " + url);  // In log kiểm tra URL

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());

                if (response.statusCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(response.body());
                    JSONArray dataArray = jsonResponse.getJSONArray("data");

                    List<ClassModel> list = new ArrayList<>();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        ClassModel classModel = new ClassModel(
                                item.optString("_id"),
                                item.optString("name"),
                                item.optString("code"),
                                item.optString("description"),
                                item.optString("createdAt"),
                                item.optString("updatedAt")
                        );
                        list.add(classModel);
                    }
                    return list;
                } else {
                    throw new RuntimeException("Lỗi HTTP: " + response.statusCode());
                }
            }
        };

        task.setOnSucceeded(e -> classesList.setAll(task.getValue()));
        task.setOnFailed(e -> {
            Throwable exception = task.getException();
            exception.printStackTrace();
            showAlert("Lỗi: " + exception.getMessage());
        });

        new Thread(task).start();
    }

    @FXML
    private void handleNewClass() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/newClass.fxml"));
            AnchorPane newPage = loader.load();
            contentArea.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở trang tạo tài khoản.");
        }
    }
    
    @FXML
    private void handleUpdateClass() {
        ClassModel selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một tài khoản để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateClass.fxml"));
            AnchorPane updatePage = loader.load();

            UpdateClassController controller = loader.getController();
            controller.setClassModel(selected);

            contentArea.getChildren().setAll(updatePage);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật tài khoản.");
        }
    }

    // @FXML
    // private void updateClass() {
    //     ClassModel selected = classTable.getSelectionModel().getSelectedItem();
    //     if (selected == null) {
    //         showAlert("Vui lòng chọn lớp để cập nhật.");
    //         return;
    //     }

    //     try {
    //         FXMLLoader loader = new FXMLLoader(getClass().getResource("updateClass.fxml"));
    //         Parent updatePage = loader.load();
    //         UpdateClassController controller = loader.getController();
    //         controller.setClassModel(selected);

    //         contentArea.getChildren().setAll(updatePage);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         showAlert("Không thể mở trang cập nhật lớp.");
    //     }
    // }

    @FXML
    private void handleDeleteClass() {
        ClassModel selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một tài khoản để xóa.");
            return;
        }
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deleteClass.fxml"));
            AnchorPane deletePage = loader.load();
    
            DeleteClassController controller = loader.getController();
            controller.setClassModel(selected);
    
            contentArea.getChildren().setAll(deletePage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở trang xác nhận xóa.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        String field = choiceBox.getValue();

        if (keyword.isEmpty() || field == null) {
            classesTable.setItems(classesList);
            return;
        }

        ObservableList<ClassModel> filtered = FXCollections.observableArrayList();

        for (ClassModel acc : classesList) {
            String value = switch (field) {
                case "id" -> acc.getId();
                case "code" -> acc.getCode();
                case "description" -> acc.getDescription();
                default -> "";
            };
            if (value.toLowerCase().contains(keyword)) {
                filtered.add(acc);
            }
        }

        classesTable.setItems(filtered);
    }

    // private void loadPage(String fxmlFile) {
    //     try {
    //         AnchorPane pane = FXMLLoader.load(getClass().getResource(fxmlFile));
    //         contentArea.getChildren().setAll(pane);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         showAlert("Không thể mở trang.");
    //     }
    // }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
