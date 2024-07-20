package com.kudelych.medicalguide.presentation.controller;

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
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MedicinesController implements LanguageUpdatable {

  @FXML
  private Label errorLabel;

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
  private Button addToSaved;

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
  private GridPane medicinesGridPane;

  @FXML
  private ScrollPane medicinesScrollPane;

  @FXML
  private TextField searchTextField;


  private MedicinesRepositoryImpl medicinesRepository;
  private Medicine selectedMedicine;
  private List<Medicine> savedMedicines;

  public MedicinesController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    searchTextField.setOnKeyReleased(event -> searchMedicines());
    loadMedicines();
  }

  private void loadMedicines() {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    savedMedicines = medicinesRepository.findSavedMedicinesByUserId(currentUser.id());
    List<Medicine> medicines = medicinesRepository.findAll();
    displayMedicineCards(medicines);
  }

  private void displayMedicineCards(List<Medicine> medicines) {
    medicinesGridPane.getChildren().clear();
    if (medicines.isEmpty()) {
      errorLabel.setText("На жаль, таких ліків немає");
      medicinesScrollPane.setVisible(false);
      return;
    } else {
      errorLabel.setText("");
      medicinesScrollPane.setVisible(true);
    }

    int column = 0;
    int row = 0;
    int cardsPerRow = 3;

    medicinesGridPane.getColumnConstraints().clear();
    medicinesGridPane.getRowConstraints().clear();

    for (int i = 0; i < cardsPerRow; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / cardsPerRow);
      medicinesGridPane.getColumnConstraints().add(columnConstraints);
    }

    for (int i = 0; i < (int) Math.ceil((double) medicines.size() / cardsPerRow); i++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(200);
      medicinesGridPane.getRowConstraints().add(rowConstraints);
    }

    for (Medicine medicine : medicines) {
      AnchorPane card = loadMedicineCard(medicine);
      if (card != null) {
        card.setOnMouseClicked(event -> displayMedicineDetails(medicine));
        medicinesGridPane.add(card, column, row);
        column++;
        if (column == cardsPerRow) {
          column = 0;
          row++;
        }
      } else {
        System.err.println("Помилка завантаження карточки для лікарського засобу: " + medicine.name());
      }
    }
  }

  private AnchorPane loadMedicineCard(Medicine medicine) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicineCard.fxml"));
      AnchorPane card = loader.load();
      MedicineCardController controller = loader.getController();
      if (controller != null) {
        controller.setMedicine(medicine);
        controller.setParentController(this); // Set parent controller
        return card;
      } else {
        System.err.println("Контролер MedicineCardController є null");
        return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Помилка завантаження medicineCard.fxml");
      return null;
    }
  }

  private void searchMedicines() {
    String query = searchTextField.getText().toLowerCase().trim();
    List<Medicine> allMedicines = medicinesRepository.findAll();
    List<Medicine> result = allMedicines.stream()
        .filter(medicine -> medicine.name().toLowerCase().contains(query))
        .collect(Collectors.toList());
    displayMedicineCards(result);
  }

  @FXML
  private void handleSaveAction() {
    ResourceBundle bundle = LanguageManager.getBundle();
    if (selectedMedicine != null) {
      saveMedicine(selectedMedicine, bundle);
    } else {
      AlertController.showAlert(bundle.getString("save.medicine.title"), bundle.getString("save.medicine.no.selection"));
    }
  }

  public void saveMedicine(Medicine medicine, ResourceBundle bundle) {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    if (savedMedicines.stream().anyMatch(savedMedicine -> savedMedicine.id() == medicine.id())) {
      AlertController.showAlert(bundle.getString("save.medicine.title"), bundle.getString("save.medicine.exists"));
    } else {
      medicinesRepository.addSavedMedicine(currentUser.id(), medicine.id());
      String successMessage = MessageFormat.format(bundle.getString("save.medicine.success"), medicine.name());
      AlertController.showAlert(bundle.getString("save.medicine.title"), successMessage);
      loadMedicines(); // Оновлення списку збережених ліків
    }
  }

  private void displayMedicineDetails(Medicine medicine) {
    selectedMedicine = medicine;
    medicineName.setText(medicine.name());
    medicineDescription.setText(medicine.description());
    medicineManufacturer.setText(medicine.manufacturer());
    medicineForm.setText(medicine.form());
    medicinePurpose.setText(medicine.purpose());

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
    searchTextField.setPromptText(bundle.getString("field.search"));
    medicineNameLabel.setText(bundle.getString("label.name"));
    medicineDescriptionLabel.setText(bundle.getString("label.description"));
    medicineManufacturerLabel.setText(bundle.getString("label.manufacturer"));
    medicineFormLabel.setText(bundle.getString("label.form"));
    medicinePurposeLabel.setText(bundle.getString("label.purpose"));
    medicineCategoryLabel.setText(bundle.getString("label.category"));
    addToSaved.setText(bundle.getString("button.addToSaved"));
  }
}
