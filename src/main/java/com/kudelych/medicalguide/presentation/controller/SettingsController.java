package com.kudelych.medicalguide.presentation.controller;

import atlantafx.base.theme.*;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.domain.setting.ThemeManager;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsController implements LanguageUpdatable {

  @FXML
  private Button changeAccountButton;

  @FXML
  private RadioButton cupertinoDarkRadioButton;

  @FXML
  private RadioButton darkThemeRadioButton;

  @FXML
  private RadioButton draculaRadioButton;

  @FXML
  private RadioButton englishRadioButton;

  @FXML
  private RadioButton germanRadioButton;

  @FXML
  private RadioButton lightThemeRadioButton;

  @FXML
  private RadioButton nordDarkThemeRadioButton;

  @FXML
  private RadioButton nordLightRadioButton;

  @FXML
  private RadioButton ukrainianRadioButton;

  @FXML
  private Label languageLabel;

  @FXML
  private Label themesLabel;

  @FXML
  private AnchorPane gerCard;

  @FXML
  private AnchorPane ukCard;

  @FXML
  private AnchorPane engCard;

  private String theme;
  private ToggleGroup themeToggleGroup;
  private ToggleGroup languageToggleGroup;

  @FXML
  void initialize() {
    theme = ThemeManager.getCurrentTheme() != null ?
        ThemeManager.getCurrentTheme() :
        new PrimerLight().getUserAgentStylesheet();
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    setInitialTheme();
    setupThemeToggleGroup();
    setupLanguageToggleGroup();

    // Обробники подій для карток
    ukCard.setOnMouseClicked(event -> selectLanguage(ukrainianRadioButton));
    engCard.setOnMouseClicked(event -> selectLanguage(englishRadioButton));
    gerCard.setOnMouseClicked(event -> selectLanguage(germanRadioButton));

    Locale locale = LanguageManager.getCurrentLocale() != null ?
        LanguageManager.getCurrentLocale() :
        new Locale("uk", "UA"); // Встановлюємо мову за замовчуванням

    setInitialLanguage(locale);
    themeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> applyTheme());
    languageToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> applyLanguage());
    changeAccountButton.setOnAction(event -> handleChangeAccountAction());
  }

  private void selectLanguage(RadioButton radioButton) {
    languageToggleGroup.selectToggle(radioButton);
  }

  private void setInitialTheme() {
    if (theme.equals(new PrimerDark().getUserAgentStylesheet())) {
      darkThemeRadioButton.setSelected(true);
    } else if (theme.equals(new PrimerLight().getUserAgentStylesheet())) {
      lightThemeRadioButton.setSelected(true);
    } else if (theme.equals(new NordLight().getUserAgentStylesheet())) {
      nordLightRadioButton.setSelected(true);
    } else if (theme.equals(new NordDark().getUserAgentStylesheet())) {
      nordDarkThemeRadioButton.setSelected(true);
    } else if (theme.equals(new CupertinoDark().getUserAgentStylesheet())) {
      cupertinoDarkRadioButton.setSelected(true);
    } else if (theme.equals(new Dracula().getUserAgentStylesheet())) {
      draculaRadioButton.setSelected(true);
    }
  }

  private void setupThemeToggleGroup() {
    themeToggleGroup = new ToggleGroup();
    lightThemeRadioButton.setToggleGroup(themeToggleGroup);
    darkThemeRadioButton.setToggleGroup(themeToggleGroup);
    nordLightRadioButton.setToggleGroup(themeToggleGroup);
    nordDarkThemeRadioButton.setToggleGroup(themeToggleGroup);
    cupertinoDarkRadioButton.setToggleGroup(themeToggleGroup);
    draculaRadioButton.setToggleGroup(themeToggleGroup);
  }

  private void setupLanguageToggleGroup() {
    languageToggleGroup = new ToggleGroup();
    englishRadioButton.setToggleGroup(languageToggleGroup);
    ukrainianRadioButton.setToggleGroup(languageToggleGroup);
    germanRadioButton.setToggleGroup(languageToggleGroup);
  }

  private void setInitialLanguage(Locale currentLanguage) {
    if (currentLanguage.equals(new Locale("en", "US"))) {
      englishRadioButton.setSelected(true);
    } else if (currentLanguage.equals(new Locale("uk", "UA"))) {
      ukrainianRadioButton.setSelected(true);
    } else if (currentLanguage.equals(new Locale("de", "DE"))) {
      germanRadioButton.setSelected(true);
    }
  }

  private void applyTheme() {
    String selectedTheme = switch ((RadioButton) themeToggleGroup.getSelectedToggle()) {
      case RadioButton rb when rb == lightThemeRadioButton -> new PrimerLight().getUserAgentStylesheet();
      case RadioButton rb when rb == darkThemeRadioButton -> new PrimerDark().getUserAgentStylesheet();
      case RadioButton rb when rb == nordLightRadioButton -> new NordLight().getUserAgentStylesheet();
      case RadioButton rb when rb == nordDarkThemeRadioButton -> new NordDark().getUserAgentStylesheet();
      case RadioButton rb when rb == cupertinoDarkRadioButton -> new CupertinoDark().getUserAgentStylesheet();
      case RadioButton rb when rb == draculaRadioButton -> new Dracula().getUserAgentStylesheet();
      default -> new PrimerLight().getUserAgentStylesheet();
    };

    Application.setUserAgentStylesheet(selectedTheme);
    setTheme(selectedTheme);
  }

  private void setTheme(String userAgentStylesheet) {
    theme = userAgentStylesheet;
    ThemeManager.setCurrentTheme(userAgentStylesheet);
  }

  private void applyLanguage() {
    Locale selectedLocale = switch ((RadioButton) languageToggleGroup.getSelectedToggle()) {
      case RadioButton rb when rb == englishRadioButton -> new Locale("en", "US");
      case RadioButton rb when rb == ukrainianRadioButton -> new Locale("uk", "UA");
      case RadioButton rb when rb == germanRadioButton -> new Locale("de", "DE");
      default -> new Locale("uk", "UA");
    };
    LanguageManager.setBundle(selectedLocale);
    ControllerManager.notifyAllControllers();
  }

  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    changeAccountButton.setText(bundle.getString("button.changeAccount"));
    themesLabel.setText(bundle.getString("label.changeThemes"));
    languageLabel.setText(bundle.getString("label.changeLanguage"));
    // Оновити всі інші текстові елементи у цьому контролері
  }

  private void handleChangeAccountAction() {
    try {
      Stage currentStage = (Stage) changeAccountButton.getScene().getWindow();
      FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentStage.getScene().getRoot());
      fadeOut.setFromValue(1.0);
      fadeOut.setToValue(0.0);
      fadeOut.setOnFinished(event -> loadNewScene(currentStage));
      fadeOut.play();
    } catch (Exception ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void loadNewScene(Stage currentStage) {
    try {
      ResourceBundle bundle = LanguageManager.getBundle();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/authorization.fxml"), bundle);
      Parent root = loader.load();

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

      currentStage.close();
    } catch (IOException ex) {
      Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}