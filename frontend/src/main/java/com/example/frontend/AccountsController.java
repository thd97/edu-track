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

    @FXML private ChoiceBox<ChoiceItem> choiceBox;
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

    @FXML private ChoiceBox<String> roleChoiceBox;

    private ObservableList<Account> accountsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList(
            new ChoiceItem("Fullname", "fullname"),
            new ChoiceItem("Username", "username")
        ));
        choiceBox.setValue(choiceBox.getItems().get(0));
        roleChoiceBox.setItems(FXCollections.observableArrayList("All role", "admin", "teacher"));
        roleChoiceBox.setValue("All role");
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
                // System.out.println("API response: " + response.body());

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
                            item.has("phoneNumber") ? item.getString("phoneNumber") : "",
                            item.has("gender") ? item.getString("gender") : ""
                        );
                        list.add(account);
                    }

                    return list;
                } else {
                    throw new RuntimeException("Error loading account list: " + response.body());
                }
            }
        };

        task.setOnSucceeded(e -> accountsList.setAll(task.getValue()));
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));

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
            showAlert("Cannot open account creation page.");
        }
    }

    @FXML
    private void handleUpdateAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/frontend/updateAccount.fxml"));
            AnchorPane updatePage = loader.load();

            UpdateAccountController controller = loader.getController();
            controller.setAccount(selected);

            contentArea.getChildren().setAll(updatePage);
        } catch (IOException e) {
            showAlert("Cannot open account update page.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        Account selected = accountsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an account to delete.");
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
            showAlert("Cannot open delete confirmation page.");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        ChoiceItem selected = choiceBox.getValue();
        String field = selected != null ? selected.getValue() : null;
        String roleValue = roleChoiceBox.getValue();
        final String role = "All role".equals(roleValue) ? "" : roleValue;

        Task<List<Account>> task = new Task<>() {
            @Override
            protected List<Account> call() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                JSONObject body = new JSONObject();
                if (field != null && !keyword.isEmpty()) {
                    body.put(field, keyword);
                }
                if (role != null && !role.isEmpty()) {
                    body.put("role", role);
                }
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ApiConstants.BASE_URL + "/api/users/filter"))
                        .header("Authorization", "Bearer " + LoginController.userToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
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
                            item.has("phoneNumber") ? item.getString("phoneNumber") : "",
                            item.has("gender") ? item.getString("gender") : ""
                        );
                        list.add(account);
                    }
                    return list;
                } else {
                    throw new RuntimeException("Error filtering accounts: " + response.body());
                }
            }
        };
        task.setOnSucceeded(e -> accountsTable.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> showAlert("Error: " + task.getException().getMessage()));
        new Thread(task).start();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
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
        public String toString() {
            return label;
        }
        public String getValue() {
            return value;
        }
    }
}
