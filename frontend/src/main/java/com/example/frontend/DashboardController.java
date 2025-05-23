package com.example.frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

public class DashboardController {

    @FXML private Button btnAccounts;
    @FXML private Button btnStudents;
    @FXML private Button btnClasses;
    @FXML private Button btnExams;
    @FXML private Button btnResults;
    @FXML private Button btnPeriod;
    @FXML private Button btnLogout;
    @FXML private AnchorPane contentArea;

    private String apiUrl;

    @FXML
    public void initialize() {
        this.apiUrl = ApiConstants.BASE_URL;

        // Ẩn nút nếu là teacher
        if ("teacher".equalsIgnoreCase(LoginController.userRole)) {
            btnAccounts.setVisible(false);
            btnAccounts.setManaged(false);
        }

        btnAccounts.setOnAction(event -> loadAccounts());
        btnClasses.setOnAction(event -> loadClasses());
        btnStudents.setOnAction(event -> loadStudents());
        btnClasses.setOnAction(event -> loadClasses());
        btnExams.setOnAction(event -> loadExams());
        btnResults.setOnAction(event -> loadExamResults());
        btnPeriod.setOnAction(event -> loadPeriod());
        btnLogout.setOnAction(event -> logout());

        if (!"teacher".equalsIgnoreCase(LoginController.userRole)) {
            loadAccounts();
        } else {
            displayText("Welcome to the Dashboard!");
        }
    }

    private void displayText(String content) {
        contentArea.getChildren().clear();
        Label label = new Label(content);
        label.setStyle("-fx-font-size: 16px;");
        contentArea.getChildren().add(label);
    }

    private void loadAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/account.fxml"));
            BorderPane accountPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(accountPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Error: " + e.getMessage());
        }
    }

    private void loadStudents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/students.fxml"));
            BorderPane studentsPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(studentsPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Error loading Students page: " + e.getMessage());
        }
    }
    private void loadClasses() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/class.fxml"));
            BorderPane classesPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(classesPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Error: " + e.getMessage());
        }
    }
    private void loadExams() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/exam.fxml"));
            BorderPane examPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(examPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Error loading Exam page: " + e.getMessage());
        }
    }
    private void loadExamResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/examResult.fxml"));
            BorderPane examResultPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(examResultPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Lỗi khi tải trang Exam Result: " + e.getMessage());
        }
    }
    private void loadPeriod() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/period.fxml"));
            BorderPane periodPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(periodPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Error loading Period page: " + e.getMessage());
        }
    }
    private void loadData(String endpoint) {
        Task<List<MyDataModel>> task = new Task<>() {
            @Override
            protected List<MyDataModel> call() throws Exception {
                return callApi(endpoint);
            }
        };

        task.setOnSucceeded(e -> {
            List<MyDataModel> data = task.getValue();
            TableView<MyDataModel> tableView = createTable(data);
            contentArea.getChildren().clear();
            contentArea.getChildren().add(tableView);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            displayText("Error: " + (ex != null ? ex.getMessage() : "Unknown error"));
        });

        new Thread(task).start();
    }

    private List<MyDataModel> callApi(String endpoint) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl + endpoint).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == 200) {
            try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                return new Gson().fromJson(reader, new TypeToken<List<MyDataModel>>(){}.getType());
            }
        } else {
            throw new RuntimeException("HTTP Error: " + connection.getResponseCode());
        }
    }

    private TableView<MyDataModel> createTable(List<MyDataModel> data) {
        TableView<MyDataModel> table = new TableView<>();
        TableColumn<MyDataModel, String> col1 = new TableColumn<>("Name");
        col1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<MyDataModel, String> col2 = new TableColumn<>("Value");
        col2.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.getColumns().addAll(col1, col2);
        table.getItems().addAll(data);
        return table;
    }

    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/Login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Logout error: " + e.getMessage());
        }
    }
}
