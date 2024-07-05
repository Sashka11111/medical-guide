package com.kudelych.medicalguide.presentation.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertController {

  @FXML
  private ImageView icon;
  @FXML
  private Label messageLabel;
  @FXML
  private Button okButton;

  private Stage stage;

  @FXML
  private void handleOkAction() {
    stage.close();
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public void setMessage(String message) {
    messageLabel.setText(message);
  }

  public void setIcon(String iconPath) {
    icon.setImage(new Image(iconPath));
  }

  public static void showAlert(String title, String message) {
    try {
      FXMLLoader loader = new FXMLLoader(AlertController.class.getResource("/view/alert.fxml"));
      AnchorPane root = loader.load();
      AlertController controller = loader.getController();
      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.UNDECORATED);
      stage.setTitle(title);
      Scene scene = new Scene(root);
      stage.setScene(scene);
      controller.setStage(stage);
      controller.setMessage(message);
      stage.showAndWait();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
