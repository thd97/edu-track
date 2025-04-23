package controllers;

import frontend.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AccountsController {

    @FXML
    private TableView<Account> accountsTable;

    @FXML
    private TableColumn<Account, Integer> idColumn;

    @FXML
    private TableColumn<Account, String> usernameColumn;

    @FXML
    private TableColumn<Account, String> passwordColumn;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    public void initialize() {
        // Thiết lập các cột cho bảng
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());

        // Thêm logic cho các nút
        btnNew.setOnAction(event -> createNewAccount());
        btnUpdate.setOnAction(event -> updateAccount());
        btnDelete.setOnAction(event -> deleteAccount());
    }

    private void createNewAccount() {
        // Logic để tạo tài khoản mới
        System.out.println("Create New Account");
    }

    private void updateAccount() {
        // Logic để cập nhật tài khoản
        System.out.println("Update Account");
    }

    private void deleteAccount() {
        // Logic để xóa tài khoản
        System.out.println("Delete Account");
    }
}
