package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.security.AuthenticatedUser;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import java.io.ByteArrayInputStream;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.List;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SavedMedicinesController implements LanguageUpdatable {
  @FXML
  private Label errorLabel;
  @FXML
  private Button deleteFromSaved;

  @FXML
  private TextFlow medicineCategoriesTextFlow;

  @FXML
  private Label medicineCategoryLabel;

  @FXML
  private Label medicineDescription;

  @FXML
  private Label medicineDescriptionLabel;

  @FXML
  private Label medicineForm;

  @FXML
  private Label medicineFormLabel;

  @FXML
  private ImageView medicineImageView;

  @FXML
  private Label medicineManufacturer;

  @FXML
  private Label medicineManufacturerLabel;

  @FXML
  private Label medicineName;

  @FXML
  private Label medicineNameLabel;

  @FXML
  private Label medicinePurpose;

  @FXML
  private Label medicinePurposeLabel;

  @FXML
  private GridPane savedMedicinesGridPane;
  private MedicinesRepositoryImpl medicinesRepository;
  private Medicine selectedMedicine;

  public SavedMedicinesController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize()
  {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    loadSavedMedicines();
  }

  // Завантаження збережених ліків для поточного користувача
  private void loadSavedMedicines() {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    List<Medicine> savedMedicines = medicinesRepository.findSavedMedicinesByUserId(currentUser.id());
    displaySavedMedicineCards(savedMedicines);
  }

  // Відображення карток збережених ліків
  private void displaySavedMedicineCards(List<Medicine> medicines) {
    savedMedicinesGridPane.getChildren().clear();
    if (medicines.isEmpty()) {
      ResourceBundle bundle = LanguageManager.getBundle();
      errorLabel.setText(bundle.getString("message.noSavedMedicines"));
      return;
    }
    int column = 0;
    int row = 0;
    int cardsPerRow = 3;

    savedMedicinesGridPane.getColumnConstraints().clear();
    savedMedicinesGridPane.getRowConstraints().clear();

    // Налаштування стовпців для сітки збережених ліків
    for (int i = 0; i < cardsPerRow; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / cardsPerRow);
      savedMedicinesGridPane.getColumnConstraints().add(columnConstraints);
    }

    // Налаштування рядків для сітки збережених ліків
    for (int i = 0; i < (int) Math.ceil((double) medicines.size() / cardsPerRow); i++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(200);
      savedMedicinesGridPane.getRowConstraints().add(rowConstraints);
    }

    // Додавання карток ліків до сітки
    for (Medicine medicine : medicines) {
      AnchorPane card = loadMedicineCard(medicine);
      if (card != null) {
        card.setOnMouseClicked(event -> displayMedicineDetails(medicine));
        savedMedicinesGridPane.add(card, column, row);
        column++;
        if (column == cardsPerRow) {
          column = 0;
          row++;
        }
      } else {
        System.err.println("Помилка завантаження карти для лікарського засобу: " + medicine.name());
      }
    }
  }

  // Завантаження карти лікарського засобу
  private AnchorPane loadMedicineCard(Medicine medicine) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicineCard.fxml"));
      AnchorPane card = loader.load();
      MedicineCardController controller = loader.getController();
      if (controller != null) {
        controller.setMedicine(medicine);
        return card;
      } else {
        System.err.println("Помилка: Об'єкт контролера MedicineCardController є null");
        return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Помилка завантаження medicineCard.fxml");
      return null;
    }
  }

  // Обробник події видалення лікарського засобу
  @FXML
  private void handleDeleteAction() {
    if (selectedMedicine != null) {
      deleteMedicine(selectedMedicine);
    } else {
      ResourceBundle bundle = LanguageManager.getBundle();
      AlertController.showAlert(bundle.getString("alert.deleteMedicineTitle"), bundle.getString("alert.noMedicineSelected"));
    }
  }

  // Видалення лікарського засобу зі списку збережених
  public void deleteMedicine(Medicine medicine) {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    try {
      medicinesRepository.removeSavedMedicine(currentUser.id(), medicine.id());
      ResourceBundle bundle = LanguageManager.getBundle();
      AlertController.showAlert(bundle.getString("alert.deleteMedicineTitle"), bundle.getString("alert.medicineDeleted") + " " + medicine.name());
      loadSavedMedicines();
      clearMedicineDetails();
    } catch (EntityNotFoundException e) {
      ResourceBundle bundle = LanguageManager.getBundle();
      AlertController.showAlert(bundle.getString("alert.deleteErrorTitle"), bundle.getString("alert.medicineNotFound"));
    }
  }

  // Очищення полів з інформацією про лікарський засіб
  private void clearMedicineDetails() {
    selectedMedicine = null;
    medicineName.setText("");
    medicineDescription.setText("");
    medicineManufacturer.setText("");
    medicineForm.setText("");
    medicinePurpose.setText("");
    medicineCategoriesTextFlow.getChildren().clear();
    Image defaultImage = new Image(getClass().getResourceAsStream("/data/icon.png"));
    medicineImageView.setImage(defaultImage);
  }

  // Відображення деталей обраного лікарського засобу
  private void displayMedicineDetails(Medicine medicine) {
    selectedMedicine = medicine;
    medicineName.setText(medicine.name());
    medicineDescription.setText(medicine.description());
    medicineManufacturer.setText(medicine.manufacturer());
    medicineForm.setText(medicine.form());
    medicinePurpose.setText(medicine.purpose());

    // Відображення категорій лікарського засобу
    List<Category> categories = medicinesRepository.getCategoriesByMedicineId(medicine.id());
    String categoriesText = categories.stream()
        .map(Category::name)
        .collect(Collectors.joining(", "));
    Text categoriesTextElement = new Text(categoriesText);
    medicineCategoriesTextFlow.getChildren().clear();
    medicineCategoriesTextFlow.getChildren().add(categoriesTextElement);
    byte[] imageBytes = medicine.image();
    if (imageBytes != null && imageBytes.length > 0) {
      Image image = new Image(new ByteArrayInputStream(imageBytes));
      medicineImageView.setImage(image);
    } else {
      medicineImageView.setImage(new Image(getClass().getResourceAsStream("/data/icon.png")));
    }
  }

  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    // Оновлення тексту для полів, що показують інформацію про лікарський засіб
    medicineNameLabel.setText(bundle.getString("label.name"));
    medicineDescriptionLabel.setText(bundle.getString("label.description"));
    medicineManufacturerLabel.setText(bundle.getString("label.manufacturer"));
    medicineFormLabel.setText(bundle.getString("label.form"));
    medicinePurposeLabel.setText(bundle.getString("label.purpose"));
    medicineCategoryLabel.setText(bundle.getString("label.category"));
    deleteFromSaved.setText(bundle.getString("button.deleteFromSaved"));
    if (errorLabel.getText().equals("Поки у Вас немає збережених ліків")) {
      errorLabel.setText(bundle.getString("message.noSavedMedicines"));
    }
  }
}
