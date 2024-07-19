package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import com.kudelych.medicalguide.persistence.repository.contract.UserRepository;
import com.kudelych.medicalguide.persistence.repository.impl.UserRepositoryImpl;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import javafx.scene.image.Image;

public class UsersManagementController implements LanguageUpdatable {

  @FXML
  private TableView<User> usersTableView;
  @FXML
  private TableColumn<User, String> usernameColumn;
  @FXML
  private TableColumn<User, String> roleColumn;

  @FXML
  private ComboBox<UserRole> roleComboBox;

  @FXML
  private Button changeRoleButton;
  @FXML
  private TextField usernameField;
  @FXML
  private Button deleteButton;

  private final UserRepository userRepository;

  public UsersManagementController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  private void initialize() {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().username()));
    roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().role().toString()));

    loadUsers();
    loadRoles();

    usersTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillForm(newValue));

    changeRoleButton.setOnAction(event -> changeRole());
    deleteButton.setOnAction(event -> deleteUser());
  }

  private void loadUsers() {
    List<User> users = userRepository.findAll();
    ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
    usersTableView.setItems(userObservableList);
  }

  private void loadRoles() {
    roleComboBox.getItems().setAll(UserRole.values());
  }

  private void fillForm(User user) {
    if (user != null) {
      usernameField.setText(user.username());
      roleComboBox.setValue(user.role());
    }
  }

  private void changeRole() {
    String username = usernameField.getText();
    UserRole newRole = roleComboBox.getValue();
    if (username != null && !username.isEmpty() && newRole != null) {
      try {
        userRepository.updateUserRole(username, newRole);
        AlertController.showAlert("Успіх","Роль користувача змінено");
        loadUsers();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Помилка", e.getMessage());
      }
    } else {
      AlertController.showAlert("Попередження", "Будь ласка, заповніть всі поля");
    }
    clearFields();
  }

  private void deleteUser() {
    String username = usernameField.getText();
    if (username != null && !username.isEmpty()) {
      try {
        userRepository.deleteUser(username);
        AlertController.showAlert("Успіх", "Користувача видалено");
        loadUsers();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Помилка", e.getMessage());
      }
    } else {
      AlertController.showAlert("Попередження", "Будь ласка, введіть ім'я користувача");
    }
    clearFields();
  }
  private void clearFields() {
    usernameField.clear();
    roleComboBox.setValue(null);
  }
  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    changeRoleButton.setText(bundle.getString("button.changeRole"));
    deleteButton.setText(bundle.getString("button.deleteUser"));
    usernameField.setPromptText(bundle.getString("field.username"));
    roleComboBox.setPromptText(bundle.getString("comboBox.role"));
    usernameColumn.setText(bundle.getString("column.username"));
    roleColumn.setText(bundle.getString("column.role"));
  }

}
