package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.security.PasswordHashing;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.domain.validation.UserValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import com.kudelych.medicalguide.persistence.repository.contract.UserRepository;
import com.kudelych.medicalguide.persistence.repository.impl.UserRepositoryImpl;
import com.kudelych.medicalguide.presentation.animation.Shake;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController implements LanguageUpdatable {

  @FXML
  private Button SignInButton;

  @FXML
  private Hyperlink SignUpHyperlink;

  @FXML
  private Button btn_close;

  @FXML
  private Label errorMessageLabel;

  @FXML
  private TextField loginField;

  @FXML
  private Label medGuide;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Label regLabel;

  @FXML
  private Label regQuestion;

  private UserRepository userRepository;

  public RegistrationController() {
    this.userRepository = new UserRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
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
        errorMessageLabel.setText(getLocalizedMessage("error.empty.fields"));
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
          errorMessageLabel.setText(getLocalizedMessage("error.username.exists", username));
          Shake userLoginAnim = new Shake(loginField);
          userLoginAnim.playAnim();
        }
      } else {
        errorMessageLabel.setText(getLocalizedMessage("error.password.invalid"));
        Shake userLoginAnim = new Shake(loginField);
        Shake userPassAnim = new Shake(passwordField);
        userLoginAnim.playAnim();
        userPassAnim.playAnim();
      }
    });
  }

  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    loginField.setPromptText(bundle.getString("field.loginField"));
    passwordField.setPromptText(bundle.getString("field.passwordField"));
    SignUpHyperlink.setText(bundle.getString("hyperLink.SignUpHyperlink"));
    SignInButton.setText(bundle.getString("label.SignInButton"));
    medGuide.setText(bundle.getString("label.medicalGuide"));
    regLabel.setText(bundle.getString("label.reg"));
    regQuestion.setText(bundle.getString("label.regQuestion"));
  }

  private String getLocalizedMessage(String key, Object... args) {
    ResourceBundle bundle = LanguageManager.getBundle();
    return String.format(bundle.getString(key), args);
  }
}
