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
    private TableView<ClassModel> classesTable;

    @FXML
    private TableColumn<ClassModel, String> idColumn;

    @FXML
    private TableColumn<ClassModel, String> nameColumn;

    @FXML
    private TableColumn<ClassModel, String> teacherNameColumn;

    @FXML
    private TableColumn<ClassModel, String> totalStudentColumn;

    @FXML
    private AnchorPane contentArea;

    @FXML
    private ComboBox<ChoiceItem> choiceBox;

    @FXML private Button btnNew, btnUpdate, btnDelete;

    @FXML
    private TextField searchField;

    private ObservableList<ClassModel> classesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList(
            new ChoiceItem("Class Name", "name"),
            new ChoiceItem("Teacher Name", "teacherName")
        ));
        choiceBox.setValue(choiceBox.getItems().get(0));
        setupTable();
        fetchClasses();

        btnNew.setOnAction(e -> handleNewClass());
        btnUpdate.setOnAction(e -> handleUpdateClass());
        btnDelete.setOnAction(e -> handleDeleteClass());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        nameColumn.setCellValueFactory(cell -> cell.getValue().nameProperty());
        teacherNameColumn.setCellValueFactory(cell -> cell.getValue().teacherNameProperty());
        totalStudentColumn.setCellValueFactory(cell -> cell.getValue().totalStudentProperty());

        classesTable.setItems(classesList);
    }

    private void fetchClasses() {
        Task<List<ClassModel>> task = new Task<>() {
            @Override
            protected List<ClassModel> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                String url = ApiConstants.GET_CLASSES_API + "?limit=100&page=1";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                List<ClassModel> list = new ArrayList<>();
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject item = dataArray.getJSONObject(i);
                    JSONObject teacherObj = item.optJSONObject("teacher");
                    String teacherName = teacherObj != null ? teacherObj.optString("fullName", "") : "";
                    int totalStudent = item.has("totalStudent") ? item.getInt("totalStudent") : 0;
                    ClassModel classModel = new ClassModel(
                        item.optString("_id"),
                        item.optString("name"),
                        "",
                        "",
                        item.optString("createdAt", ""),
                        item.optString("updatedAt", ""),
                        teacherName,
                        String.valueOf(totalStudent)
                    );
                    list.add(classModel);
                }
                return list;
            }
        };
        task.setOnSucceeded(e -> classesList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));
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
            showAlert("Cannot open new account page.");
        }
    }
    
    @FXML
    private void handleUpdateClass() {
        ClassModel selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a class to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateClass.fxml"));
            AnchorPane updatePage = loader.load();

            UpdateClassController controller = loader.getController();
            controller.setClassModel(selected);

            contentArea.getChildren().setAll(updatePage);
        } catch (IOException e) {
            e.printStackTrace();
            // showAlert(e);
        }
    }
    @FXML
    private void handleDeleteClass() {
        ClassModel selected = classesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a class to delete.");
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
            showAlert("Cannot open delete confirmation page.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();
        ChoiceItem selected = choiceBox.getValue();
        String field = selected != null ? selected.getValue() : null;
        if (keyword.isEmpty() || field == null) {
            classesTable.setItems(classesList);
            return;
        }
        ObservableList<ClassModel> filtered = FXCollections.observableArrayList();
        for (ClassModel cls : classesList) {
            String value = switch (field) {
                case "name" -> cls.getName();
                case "teacherName" -> cls.getTeacherName();
                default -> "";
            };
            if (value.toLowerCase().contains(keyword)) {
                filtered.add(cls);
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
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ChoiceItem {
        private final String label;
        private final String value;
        public ChoiceItem(String label, String value) {
            this.label = label;
            this.value = value;
        }
        @Override
        public String toString() { return label; }
        public String getValue() { return value; }
    }
}
