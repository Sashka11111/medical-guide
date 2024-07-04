package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainMenuController {

  @FXML
  private Button medicinesButton;
  @FXML
  private Button bookmarksButton;
  @FXML
  private Button categoryButton;
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

  private Stage stage;
  private double xOffset = 0;
  private double yOffset = 0;

  @FXML
  void initialize() {
    closeButton.setOnAction(event -> System.exit(0));
    minimazeButton.setOnAction(event -> minimizeWindow());
    Medicines();
    medicinesButton.setOnAction(event -> showMedicinesPage());
    categoryButton.setOnAction(event -> showCategoryPage());
    bookmarksButton.setOnAction(event -> showSavedMedicinePage());
    changeAccountButton.setOnAction(event -> handleChangeAccountAction());

    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    userName.setText(currentUser.username());

    // Перевіряє роль користувача та приховує кнопку категорії, якщо це не адміністратор
    if (currentUser.role() != UserRole.ADMIN) {
      categoryButton.setVisible(false);
    }

    // Platform.runLater для додавання обробників після ініціалізації сцени
    Platform.runLater(() -> {
      Stage primaryStage = (Stage) contentArea.getScene().getWindow();
      addDragListeners(primaryStage.getScene().getRoot());
    });
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
    moveStackPane(bookmarksButton);
    loadFXML("/view/savedMedicine.fxml");
  }

  private void showCategoryPage() {
    moveStackPane(categoryButton);
    loadFXML("/view/category.fxml");
  }

  private void loadFXML(String fxmlFileName) {
    try {
      Parent fxml = FXMLLoader.load(getClass().getResource(fxmlFileName));
      contentArea.getChildren().clear();
      contentArea.getChildren().add(fxml);
    } catch (IOException ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void Medicines() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicines.fxml"));
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

  private void handleChangeAccountAction() {
    try {
      // Анімація для поточного вікна
      Stage currentStage = (Stage) changeAccountButton.getScene().getWindow();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      fadeOut.setOnFinished(event -> {
        try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"));
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
}
