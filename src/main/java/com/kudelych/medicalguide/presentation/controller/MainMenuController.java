package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.security.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
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
  private Button usersManagementButton;

  @FXML
  private Button settingsButton;

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
    savedMedicineButton.setOnAction(event -> showSavedMedicinePage());
    manageMedicinesButton.setOnAction(event -> showManageMedicinesPage());
    usersManagementButton.setOnAction(event -> showUsersPage());
    settingsButton.setOnAction(event -> showSettingsPage());

    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    userName.setText(currentUser.username());

    if (currentUser.role() != UserRole.ADMIN) {
      categoryButton.setVisible(false);
      manageMedicinesButton.setVisible(false);
      usersManagementButton.setVisible(false);
    }

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
  private void showUsersPage() {
    moveStackPane(usersManagementButton);
    loadFXML("/view/usersManagement.fxml");
  }
  private void showSettingsPage() {
    moveStackPane(settingsButton);
    loadFXML("/view/settings.fxml");
  }

  private void loadFXML(String fxmlFileName) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
      Parent fxml = loader.load();
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
}
