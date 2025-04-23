package controllers;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.concurrent.Task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import frontend.MyDataModel;

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
    private String userType; // Biến để lưu loại tài khoản

    @FXML
    public void initialize() {
        Dotenv dotenv = Dotenv.configure().directory("backend").load();
        apiUrl = dotenv.get("API_URL");

        btnAccounts.setOnAction(event -> loadAccounts());
        btnTeachers.setOnAction(event -> loadData("/teachers"));
        btnStudents.setOnAction(event -> loadData("/students"));
        btnClasses.setOnAction(event -> loadData("/classes"));
        btnExams.setOnAction(event -> loadData("/exams"));
        btnResults.setOnAction(event -> loadData("/results"));
        btnAttendance.setOnAction(event -> loadData("/attendance"));
        btnLogout.setOnAction(event -> logout());
        btnFilter.setOnAction(event -> txtSearch.clear());

        // Mặc định load trang dashboard ban đầu
        displayText("Welcome to the Dashboard!");
    }

    // Phương thức để thiết lập loại tài khoản
    public void setUserType(String userType) {
        this.userType = userType;
        if ("teacher".equals(userType) || "student".equals(userType)) {
            btnAccounts.setVisible(false);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/account.fxml"));
            BorderPane accountPane = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(accountPane);
        } catch (IOException e) {
            e.printStackTrace();
            displayText("Lỗi: " + e.getMessage());
        }
    }

    private void loadData(String endpoint) {
        Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
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
            try (InputStream inputStream = connection.getInputStream();
                 Reader reader = new InputStreamReader(inputStream)) {
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

        table.setPrefSize(600, 400);
        return table;
    }

    private void logout() {
        displayText("Đã đăng xuất");
        // Bạn có thể thêm logic logout thật tại đây
    }
}