package com.example.frontend;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AccountsController {

    @FXML private ChoiceBox<String> choiceBox;
    @FXML private TextField searchField;
    @FXML private TableView<Account> accountsTable;

    @FXML private TableColumn<Account, String> idColumn;
    @FXML private TableColumn<Account, String> fullNameColumn;
    @FXML private TableColumn<Account, String> usernameColumn;
    @FXML private TableColumn<Account, String> passwordColumn;
    @FXML private TableColumn<Account, String> emailColumn;
    @FXML private TableColumn<Account, String> roleColumn;
    @FXML private TableColumn<Account, String> phoneNumberColumn;
    @FXML private TableColumn<Account, String> genderColumn;

    @FXML private Button btnNew, btnUpdate, btnDelete;
    @FXML private AnchorPane contentArea;

    private ObservableList<Account> accountsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList("fullname", "username"));
        setupTable();
        fetchAccounts();

        btnNew.setOnAction(e -> handleNewAccount());
        btnUpdate.setOnAction(e -> handleUpdateAccount());
        btnDelete.setOnAction(e -> handleDeleteAccount());
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cell -> cell.getValue().idProperty());
        fullNameColumn.setCellValueFactory(cell -> cell.getValue().fullnameProperty());
        usernameColumn.setCellValueFactory(cell -> cell.getValue().usernameProperty());
        passwordColumn.setCellValueFactory(cell -> cell.getValue().passwordProperty());
        emailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        roleColumn.setCellValueFactory(cell -> cell.getValue().roleProperty());
        phoneNumberColumn.setCellValueFactory(cell -> cell.getValue().phoneNumberProperty());
        genderColumn.setCellValueFactory(cell -> cell.getValue().genderProperty());

        accountsTable.setItems(accountsList);
    }

    private void fetchAccounts() {
        Task<List<Account>> task = new Task<>() {
            @Override
            protected List<Account> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.GET_USERS_API))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("API response: " + response.body());

                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());
                    JSONArray dataArray = json.getJSONArray("data");
                    List<Account> list = new ArrayList<>();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        Account account = new Account(
                            item.getString("_id"),
                            item.getString("fullName"),
                            item.getString("username"),
                            item.has("password") ? item.getString("password") : "******",
                            item.getString("email"),
                            item.getString("role"),
                            item.getString("phoneNumber"),
                            item.getString("gender")
                        );
                        list.add(account);
                    }

                    return list;
                } else {
                    throw new RuntimeException("Lỗi khi tải danh sách tài khoản: " + response.body());
                }
            }
        };

        task.setOnSucceeded(e -> accountsList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Lỗi: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    private void handleNewAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/newAccount.fxml"));
            AnchorPane newPage = loader.load();
            contentArea.getChildren().setAll(newPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Không thể mở trang tạo tài khoản.");
        }
    }

    @FXML
    private void handleUpdateAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một tài khoản để cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateAccount.fxml"));
            AnchorPane updatePage = loader.load();

            UpdateAccountController controller = loader.getController();
            controller.setAccount(selected);

            contentArea.getChildren().setAll(updatePage);
        } catch (IOException e) {
            showAlert("Không thể mở trang cập nhật tài khoản.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Vui lòng chọn một tài khoản để xóa.");
            return;
        }
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/deleteAccount.fxml"));
            AnchorPane deletePage = loader.load();
    
            DeleteAccountController controller = loader.getController();
            controller.setAccount(selected);
    
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
            accountsTable.setItems(accountsList);
            return;
        }

        ObservableList<Account> filtered = FXCollections.observableArrayList();

        for (Account acc : accountsList) {
            String value = switch (field) {
                case "fullname" -> acc.getFullname();
                case "username" -> acc.getUsername();
                default -> "";
            };
            if (value.toLowerCase().contains(keyword)) {
                filtered.add(acc);
            }
        }

        accountsTable.setItems(filtered);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
