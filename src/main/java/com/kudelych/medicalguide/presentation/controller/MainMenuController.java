package com.kudelych.medicalguide.presentation.controller;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import com.kudelych.medicalguide.domain.security.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenuController {

  @FXML
  private Button medicinesButton;

  @FXML
  private Button savedMedicineButton;

  @FXML
  private Button categoryButton;

  @FXML
  private Button manageMedicinesButton;

  @FXML
  private Button closeButton;

  @FXML
  private Button minimazeButton;

  @FXML
  private Button changeAccountButton;

  @FXML
  private StackPane stackPane;

  @FXML
  private StackPane contentArea;

  @FXML
  private Label userName;

  @FXML
  private ToggleSwitch themeToggleSwitch;

  @FXML
  private ComboBox<String> languageComboBox;

  private Stage stage;
  private double xOffset = 0;
  private double yOffset = 0;
  private String theme = new PrimerLight().getUserAgentStylesheet();

  private ResourceBundle bundle;

  @FXML
  void initialize() {
    Application.setUserAgentStylesheet(theme);
    themeToggleSwitch.selectedProperty().addListener((observable, oldValue, newValue) -> handleTheme(newValue));

    closeButton.setOnAction(event -> System.exit(0));
    minimazeButton.setOnAction(event -> minimizeWindow());
    Medicines();
    medicinesButton.setOnAction(event -> showMedicinesPage());
    categoryButton.setOnAction(event -> showCategoryPage());
    savedMedicineButton.setOnAction(event -> showSavedMedicinePage());
    manageMedicinesButton.setOnAction(event -> showManageMedicinesPage());
    changeAccountButton.setOnAction(event -> handleChangeAccountAction());

    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    userName.setText(currentUser.username());

    // Перевіряє роль користувача та приховує кнопку категорії, якщо це не адміністратор
    if (currentUser.role() != UserRole.ADMIN) {
      categoryButton.setVisible(false);
      manageMedicinesButton.setVisible(false);
    }

    // Platform.runLater для додавання обробників після ініціалізації сцени
    Platform.runLater(() -> {
      Stage primaryStage = (Stage) contentArea.getScene().getWindow();
      addDragListeners(primaryStage.getScene().getRoot());
    });

    // Initialize language ComboBox
    languageComboBox.getItems().addAll("English", "Українська");
    languageComboBox.getSelectionModel().selectFirst();
    languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> changeLanguage(newValue));

  }

  private void moveStackPane(Button button) {
    double buttonX = button.localToParent(button.getBoundsInLocal()).getMinX();
    double buttonY = button.localToParent(button.getBoundsInLocal()).getMinY();
    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), stackPane);
    transition.setToX(buttonX);
    stackPane.setLayoutY(buttonY);
  }

  private void showMedicinesPage() {
    moveStackPane(medicinesButton);
    loadFXML("/view/medicines.fxml");
  }

  private void showSavedMedicinePage() {
    moveStackPane(savedMedicineButton);
    loadFXML("/view/savedMedicine.fxml");
  }

  private void showManageMedicinesPage() {
    moveStackPane(manageMedicinesButton);
    loadFXML("/view/medicineManagement.fxml");
  }

  private void showCategoryPage() {
    moveStackPane(categoryButton);
    loadFXML("/view/category.fxml");
  }

  private void loadFXML(String fxmlFileName) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName), bundle);
      Parent fxml = loader.load();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(fxml);
    } catch (IOException ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void Medicines() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicines.fxml"), bundle);
      AnchorPane medicinesAnchorPane = loader.load();
      MedicinesController medicinesController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(medicinesAnchorPane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void minimizeWindow() {
    if (stage == null) {
      stage = (Stage) minimazeButton.getScene().getWindow();
    }
    stage.setIconified(true);
  }

  private void addDragListeners(Parent root) {
    root.setOnMousePressed(event -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });

    root.setOnMouseDragged(event -> {
      Stage stage = (Stage) ((Parent) event.getSource()).getScene().getWindow();
      stage.setX(event.getScreenX() - xOffset);
      stage.setY(event.getScreenY() - yOffset);
    });
  }

  private void handleTheme(boolean isLight) {
    if (isLight) {
      Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
      setTheme(new PrimerLight().getUserAgentStylesheet());
    } else {
      Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
      setTheme(new PrimerDark().getUserAgentStylesheet());
    }
  }
  private void setTheme(String userAgentStylesheet) {
    theme = userAgentStylesheet;
  }

  private void handleChangeAccountAction() {
    try {
      // Анімація для поточного вікна
      Stage currentStage = (Stage) changeAccountButton.getScene().getWindow();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      fadeOut.setOnFinished(event -> {
        try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"), bundle);
          Parent root = loader.load();

          // Анімація для нового вікна
          Stage loginStage = new Stage();
          loginStage.getIcons().add(new Image(getClass().getResourceAsStream("/data/icon.png")));
          loginStage.initStyle(StageStyle.UNDECORATED);
          Scene scene = new Scene(root);
          scene.getRoot().setOpacity(0.0);
          loginStage.setScene(scene);
          loginStage.show();

          FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
          fadeIn.setFromValue(0.0);
          fadeIn.setToValue(1.0);
          fadeIn.play();

          // Закриття поточного вікна
          currentStage.close();
        } catch (IOException ex) {
          Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
      });
      fadeOut.play();
    } catch (Exception ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void changeLanguage(String language) {
    if (language.equals("English")) {
      bundle = ResourceBundle.getBundle("messages_en", new Locale("en", "US"));
    } else if (language.equals("Українська")) {
      bundle = ResourceBundle.getBundle("messages_uk", new Locale("uk", "UA"));
    }
    updateLanguage();
  }

  private void updateLanguage() {
    medicinesButton.setText(bundle.getString("button.medicines"));
    savedMedicineButton.setText(bundle.getString("button.savedMedicine"));
    categoryButton.setText(bundle.getString("button.category"));
    manageMedicinesButton.setText(bundle.getString("button.manageMedicines"));
    changeAccountButton.setText(bundle.getString("button.changeAccount"));// Додайте всі інші елементи, які потребують оновлення мови
  }
}
