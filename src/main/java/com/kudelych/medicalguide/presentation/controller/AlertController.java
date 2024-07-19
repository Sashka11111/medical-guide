package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AlertController implements LanguageUpdatable {

  @FXML
  private ImageView icon;
  @FXML
  private Label messageLabel;

  private Stage stage;
  @FXML
  void initialize() {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
  }
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
  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    messageLabel.setText(bundle.getString("label.message"));
  }
}
