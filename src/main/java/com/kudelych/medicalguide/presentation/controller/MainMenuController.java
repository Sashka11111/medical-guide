package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.entity.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController {

  @FXML
  private Button MedicinesButton;

  @FXML
  private Button btn_close;
  @FXML
  private Button btn_minimize;
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
    btn_close.setOnAction(event -> System.exit(0));
    btn_minimize.setOnAction(event -> minimizeWindow());

    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
      userName.setText(currentUser.username());
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
/*
  private void showMyActivityPage() {
    moveStackPane(btn_myActivity);
    loadFXML("/view/myActivity.fxml");
  }

  private void showMyGoalPage() {
    moveStackPane(btn_myGoals);
    loadFXML("/view/myGoals.fxml");
  }

  private void showCompleteGoalsPage() {
    moveStackPane(btn_completeGoal);
    loadFXML("/view/completeGoals.fxml");
  }

  private void showCategoryPage() {
    moveStackPane(btn_category);
    loadFXML("/view/category.fxml");
  }

  private void showStepsToGoalPage() {
    moveStackPane(btn_steps);
    loadFXML("/view/stepsToGoal.fxml");
  }

  private void showTipsPage() {
    moveStackPane(btn_tips);
    loadFXML("/view/tips.fxml");
  }

  private void showCompleteStepsPage() {
    moveStackPane(btn_completeSteps);
    loadFXML("/view/completeSteps.fxml");
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

  private void myActivity() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/myActivity.fxml"));
      AnchorPane myActivityPane = loader.load();
      //MyActivityController myActivityController = loader.getController();
      contentArea.getChildren().clear();
      contentArea.getChildren().add(myActivityPane);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
*/

  private void minimizeWindow() {
    if (stage == null) {
      stage = (Stage) btn_minimize.getScene().getWindow();
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
