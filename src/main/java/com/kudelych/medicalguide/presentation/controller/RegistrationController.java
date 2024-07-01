package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.PasswordHashing;
import com.kudelych.medicalguide.domain.validation.UserValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import com.kudelych.medicalguide.persistence.repository.contract.UserRepository;
import com.kudelych.medicalguide.persistence.repository.impl.UserRepositoryImpl;
import com.kudelych.medicalguide.presentation.animation.Shake;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

  @FXML
  private Hyperlink SignUpHyperlink;

  @FXML
  private Button SignInButton;

  @FXML
  private TextField loginField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button btn_close;

  @FXML
  private Label errorMessageLabel;

  private UserRepository userRepository;

  public RegistrationController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }
  @FXML
  void initialize() {
    btn_close.setOnAction(event -> {
      System.exit(0);
    });
    SignUpHyperlink.setOnAction(event -> {
      Scene currentScene = SignUpHyperlink.getScene();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
      try {
        Parent root = loader.load();
        currentScene.setRoot(root);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    SignInButton.setOnAction(event -> {
      String username = loginField.getText();
      String password = passwordField.getText();
      if (username.isEmpty() || password.isEmpty()) {
        errorMessageLabel.setText("Логін та пароль не повинен бути пустим");
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
        return;
      }
      if (UserValidator.isUsernameValid(username) && UserValidator.isPasswordValid(password)) {
        if (!userRepository.isUsernameExists(username)) {
          // Хешування пароля
          String hashedPassword = PasswordHashing.getInstance().hashedPassword(password);
          UserRole role = UserRole.USER;
          // Створення нового користувача
          User user = new User(0, username, hashedPassword, role);
          // Додавання користувача до бази даних через UserRepository
          userRepository.addUser(user);

          Scene currentScene = SignInButton.getScene();
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));

          try {
            Parent root = loader.load();
            currentScene.setRoot(root);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        } else {
          errorMessageLabel.setText("Логін з ім'ям " + username + " уже існує");
          Shake userLoginAnim = new Shake(loginField);
          userLoginAnim.playAnim();
        }
      } else {
        errorMessageLabel.setText("Пароль має мати велику, маленьку букву та цифру.\n" + "Мінімальна довжина паролю: 6 символів");
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
      }
    });
  }
}
