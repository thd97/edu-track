//package controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.concurrent.Task;
//import javafx.application.Platform;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.List;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//@FXML
//private TableView<MyDataModel> tableView;
//@FXML
//private TableColumn<MyDataModel, String> nameColumn;
//@FXML
//private TableColumn<MyDataModel, String> valueColumn;
//@FXML
//private Button btnAccounts;
//@FXML
//private Button btnTeachers;
//@FXML
//private Button btnStudents;
//@FXML
//private Button btnClasses;
//@FXML
//private Button btnExams;
//@FXML
//private Button btnResults;
//@FXML
//private Button btnAttentdance;
//@FXML
//private Button btnLogout;
//@FXML
//private AnchorPane contentArea;
//
//@FXML
//public void initialize() {
//    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//    valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
//    btnAccounts.setOnAction(event -> fetchDataFromAPIAccounts());
//    btnTeachers.setOnAction(event -> fetchDataFromAPITeachers());
//    btnStudents.setOnAction(event -> fetchDataFromAPIStudents());
//    btnClasses.setOnAction(event -> fetchDataFromAPIClasses());
//    btnExams.setOnAction(event -> fetchDataFromAPIExams());
//    btnResults.setOnAction(event -> fetchDataFromAPIResults());
//    btnAttentdance.setOnAction(event -> fetchDataFromAPIAttentdance());
//    btnLogout.setOnAction(event -> fetchDataFromAPILogout());
//    accountsButton.setOnAction(event -> displayAccounts());
//    teachersButton.setOnAction(event -> displayTeachers());
//    studentsButton.setOnAction(event -> displayStudents());
//    classesButton.setOnAction(event -> displayClasses());
//    examsButton.setOnAction(event -> displayExams());
//    attendanceButton.setOnAction(event -> displayAttendance());
//
//    fetchDataFromAPI();
//    fetchDataFromAPIAccounts();
//    fetchDataFromAPITeachers();
//    fetchDataFromAPIStudents();
//    fetchDataFromAPIClasses();
//    fetchDataFromAPIExams();
//    fetchDataFromAPIResults();
//    fetchDataFromAPIAttentdance();
//    fetchDataFromAPILogout();
//}
//
//private void displayAccounts() {
//    contentArea.getChildren().clear();
//    Label accountsLabel = new Label("Accounts Content");
//    contentArea.getChildren().add(accountsLabel);
//}
//
//private void displayTeachers() {
//    contentArea.getChildren().clear();
//    Label teachersLabel = new Label("Teachers Content");
//    contentArea.getChildren().add(teachersLabel);
//}
//
//private void displayStudents() {
//    contentArea.getChildren().clear();
//    Label studentsLabel = new Label("Students Content");
//    contentArea.getChildren().add(studentsLabel);
//}
//
//private void displayClasses() {
//    contentArea.getChildren().clear();
//    Label classesLabel = new Label("Classes Content");
//    contentArea.getChildren().add(classesLabel);
//}
//
//private void displayExams() {
//    contentArea.getChildren().clear();
//    Label examsLabel = new Label("Exams Content");
//    contentArea.getChildren().add(examsLabel);
//}
//
//private void displayResults() {
//    contentArea.getChildren().clear();
//    Label resultsLabel = new Label("Results Content");
//    contentArea.getChildren().add(resultsLabel);
//}
//
//private void displayAttendance() {
//    contentArea.getChildren().clear();
//    Label attendanceLabel = new Label("Attendance Content");
//    contentArea.getChildren().add(AttendanceLabel);
//}
//
//
//
//
//private void fetchDataFromAPI() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIAccounts() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api/";
//            return callApi(apiUrl);
//        }
//    };
//
//    handleTask(task);
//}
//
//private void fetchDataFromAPITeachers() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api/";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIStudents() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIClasses() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIExams() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIResults() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPIAttentdance() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private void fetchDataFromAPILogout() {
//    Task<List<MyDataModel>> task = new Task<List<MyDataModel>>() {
//        @Override
//        protected List<MyDataModel> call() throws Exception {
//            String apiUrl = "http://localhost:3004/api/login";
//            return callApi(apiUrl);
//        }
//    };
//    handleTask(task);
//}
//
//private List<MyDataModel> callApi(String apiUrl) throws Exception {
//    HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
//    connection.setRequestMethod("GET");
//
//    if (connection.getResponseCode() == 200) {
//        InputStream inputStream = connection.getInputStream();
//        Reader reader = new InputStreamReader(inputStream);
//        return new Gson().fromJson(reader, new TypeToken<List<MyDataModel>>(){}.getType());
//    } else {
//        throw new RuntimeException("Failed: HTTP error code: " + connection.getResponseCode());
//    }
//}
//
//private void handleTask(Task<List<MyDataModel>> task) {
//    task.setOnSucceeded(event -> {
//        List<MyDataModel> data = task.getValue();
//        tableView.getItems().clear();
//        tableView.getItems().addAll(data);
//    });
//
//    task.setOnFailed(event -> {
//        Throwable exception = task.getException();
//        String errorMessage = exception != null ? exception.getMessage() : "Unknown error occurred";
//
//        System.err.println("Error fetching data: " + errorMessage);
//    });
//
//    new Thread(task).start();
//}
//
//public class dashboardController {
//
//}
