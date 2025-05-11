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
    @FXML private Button btnTeachers;
    @FXML private Button btnStudents;
    @FXML private Button btnClasses;
    @FXML private Button btnExams;
    @FXML private Button btnResults;
    @FXML private Button btnAttendance;
    @FXML private Button btnLogout;
    @FXML private AnchorPane contentArea;

    @FXML private Button btnFilter;
    @FXML private Button btnListView;
    @FXML private Button btnCalendarView;
    @FXML private TextField txtSearch;

    private String apiUrl;

    @FXML
    public void initialize() {
        this.apiUrl = ApiConstants.BASE_URL;

        // Ẩn nút nếu là teacher
        if ("teacher".equalsIgnoreCase(LoginController.userRole)) {
            btnAccounts.setVisible(false);
            btnAccounts.setManaged(false);
            btnTeachers.setVisible(false);
            btnTeachers.setManaged(false);
        }

        btnAccounts.setOnAction(event -> loadAccounts());
        btnClasses.setOnAction(event -> loadClasses());
        btnTeachers.setOnAction(event -> loadData("/teachers"));
        btnStudents.setOnAction(event -> loadStudents());
        btnClasses.setOnAction(event -> loadClasses());
        btnExams.setOnAction(event -> loadExams());
        btnResults.setOnAction(event -> loadData("/results"));
        btnAttendance.setOnAction(event -> loadData("/attendance"));
        btnLogout.setOnAction(event -> logout());
        btnFilter.setOnAction(event -> txtSearch.clear());

        displayText("Welcome to the Dashboard!");
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
            displayText("Lỗi: " + e.getMessage());
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
            displayText("Lỗi khi tải trang Students: " + e.getMessage());
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
            displayText("Lỗi: " + e.getMessage());
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
            displayText("Lỗi khi tải trang Exam: " + e.getMessage());
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
            displayText("Lỗi: " + (ex != null ? ex.getMessage() : "Không rõ nguyên nhân"));
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
            throw new RuntimeException("Lỗi HTTP: " + connection.getResponseCode());
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
            displayText("Lỗi đăng xuất: " + e.getMessage());
        }
    }
}
