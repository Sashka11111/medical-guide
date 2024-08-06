package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.domain.validation.MedicinesValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.impl.CategoryRepositoryImpl;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckComboBox;
import javafx.util.StringConverter;

import java.io.*;
import java.util.List;

public class MedicineManagementController implements LanguageUpdatable {

  @FXML
  private TextField nameField;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private TextField manufacturerField;

  @FXML
  private TextField formField;

  @FXML
  private TextField purposeField;

  @FXML
  private Button clearFieldsButton;

  @FXML
  private TableView<Medicine> medicineTable;

  @FXML
  private TableColumn<Medicine, String> nameColumn;

  @FXML
  private TableColumn<Medicine, String> descriptionColumn;

  @FXML
  private TableColumn<Medicine, String> manufacturerColumn;

  @FXML
  private TableColumn<Medicine, String> formColumn;

  @FXML
  private TableColumn<Medicine, String> purposeColumn;

  @FXML
  private Button editButton;

  @FXML
  private Button deleteButton;

  @FXML
  private Button addButton;

  @FXML
  private ImageView imageView;

  @FXML
  private CheckComboBox<Category> categoryComboBox;

  @FXML
  private Label categoryLabel;

  @FXML
  private Button chooseButton;

  @FXML
  private Label descriptionLabel;

  @FXML
  private Label formLabel;

  @FXML
  private Label manufacturerLabel;

  @FXML
  private Label nameLabel;

  @FXML
  private Label photoLabel;

  @FXML
  private Label purposeLabel;

  private MedicinesRepositoryImpl medicinesRepository;
  private CategoryRepositoryImpl categoryRepository;
  private ObservableList<Medicine> medicineData = FXCollections.observableArrayList();
  private byte[] selectedImageBytes;
  private ResourceBundle bundle;

  public MedicineManagementController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  private void initialize() {
    bundle = LanguageManager.getBundle(); // Ініціалізація ResourceBundle
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    // Налаштування властивостей для колонок таблиці
    nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
    descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().description()));
    manufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().manufacturer()));
    formColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().form()));
    purposeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().purpose()));

    loadMedicines(); // Завантаження списку лікарських засобів
    loadCategories(); // Завантаження списку категорій

    // Обробник вибору рядка в таблиці
    medicineTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showMedicineDetails(newValue));
    clearFieldsButton.setOnAction(event -> clearFields()); // Обробник кнопки очищення полів
  }

  // Метод для завантаження лікарських засобів
  private void loadMedicines() {
    medicineData.setAll(medicinesRepository.findAll());
    medicineTable.setItems(medicineData);
  }

  // Метод для завантаження категорій
  private void loadCategories() {
    List<Category> categories = categoryRepository.getAllCategories();
    categoryComboBox.getItems().addAll(categories);
    categoryComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Category category) {
        return category != null ? category.name() : "";
      }

      @Override
      public Category fromString(String string) {
        return null;
      }
    });
  }

  // Метод для відображення деталей лікарського засобу
  private void showMedicineDetails(Medicine medicine) {
    if (medicine != null) {
      nameField.setText(medicine.name());
      descriptionArea.setText(medicine.description());
      manufacturerField.setText(medicine.manufacturer());
      formField.setText(medicine.form());
      purposeField.setText(medicine.purpose());
      if (medicine.image() != null) {
        imageView.setImage(new Image(new ByteArrayInputStream(medicine.image())));
        selectedImageBytes = medicine.image();
      } else {
        imageView.setImage(new Image(getClass().getResourceAsStream("/data/icon.png")));
        selectedImageBytes = null; // Зображення не вибрано
      }
      // Встановлення вибраних категорій
      categoryComboBox.getCheckModel().clearChecks();
      List<Category> medicineCategories = medicinesRepository.getCategoriesByMedicineId(medicine.id());
      for (Category category : medicineCategories) {
        categoryComboBox.getCheckModel().check(category);
      }
    } else {
      clearFields();
    }
  }

  // Обробник кнопки "Додати"
  @FXML
  private void handleAddAction() {
    Medicine newMedicine = new Medicine(
        0,
        nameField.getText(),
        descriptionArea.getText(),
        manufacturerField.getText(),
        formField.getText(),
        purposeField.getText(),
        selectedImageBytes
    );

    // Валідація нового лікарського засобу
    List<Medicine> existingMedicines = medicinesRepository.findAll();
    if (!MedicinesValidator.validateMedicine(newMedicine, existingMedicines)) {
      AlertController.showAlert(bundle.getString("error.title"), bundle.getString("error.fill.all.fields"));
      return;
    }

    if (MedicinesValidator.isMedicineNameDuplicate(newMedicine.name(), existingMedicines)) {
      AlertController.showAlert(bundle.getString("error.title"),bundle.getString("error.duplicate.name"));
      return;
    }

    List<Category> selectedCategories = categoryComboBox.getCheckModel().getCheckedItems();
    try {
      int newMedicineId = medicinesRepository.addMedicine(newMedicine);

      if (newMedicineId != -1) {
        for (Category category : selectedCategories) {
          medicinesRepository.addCategoryToMedicine(newMedicineId, category.id());
        }
      }
      loadMedicines();
      clearFields();

    } catch (EntityNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  // Обробник кнопки "Редагувати"
  @FXML
  private void handleEditAction() {
    Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
    if (selectedMedicine != null) {
      Medicine updatedMedicine = new Medicine(
          selectedMedicine.id(),
          nameField.getText(),
          descriptionArea.getText(),
          manufacturerField.getText(),
          formField.getText(),
          purposeField.getText(),
          selectedImageBytes != null ? selectedImageBytes : selectedMedicine.image()
      );

      List<Medicine> existingMedicines = medicinesRepository.findAll();
      existingMedicines.removeIf(medicine -> medicine.id() == selectedMedicine.id());

      if (MedicinesValidator.isMedicineNameDuplicate(updatedMedicine.name(), existingMedicines)) {
        AlertController.showAlert(bundle.getString("error.title"),bundle.getString("error.duplicate.name"));
        return;
      }

      try {
        medicinesRepository.updateMedicine(updatedMedicine);
        List<Integer> categoryIds = categoryRepository.getCategoriesByMedicineId(updatedMedicine.id());
        for (int categoryId : categoryIds) {
          categoryRepository.removeCategoryFromMedicine(updatedMedicine.id(), categoryId);
        }
        for (Category category : categoryComboBox.getCheckModel().getCheckedItems()) {
          categoryRepository.addCategoryToMedicine(updatedMedicine.id(), category.id());
        }
        loadMedicines();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert(bundle.getString("error.title"),bundle.getString("success.medicine.updated"));
      }
    } else {
      AlertController.showAlert(bundle.getString("error.title"),bundle.getString("error.select.medicine"));
    }
    clearFields();
  }

  // Обробник кнопки "Видалити"
  @FXML
  private void handleDeleteAction() {
    Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
    if (selectedMedicine != null) {
      try {
        List<Category> categories = medicinesRepository.getCategoriesByMedicineId(selectedMedicine.id());
        for (Category category : categories) {
          medicinesRepository.removeCategoryFromMedicine(selectedMedicine.id(), category.id());
        }
        medicinesRepository.deleteMedicine(selectedMedicine.id());
        loadMedicines();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert(bundle.getString("error.title"),bundle.getString("error.entity.not.found"));
      }
    } else {
      AlertController.showAlert(bundle.getString("error.title"),bundle.getString("error.select.medicine"));
    }
    clearFields();
  }

  // Обробник кнопки вибору зображення
  @FXML
  private void handleSelectImageAction() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(getLocalizedMessage("button.choose"), "*.png", "*.jpg", "*.jpeg"));
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null) {
      try (FileInputStream fis = new FileInputStream(selectedFile)) {
        selectedImageBytes = fis.readAllBytes();
        imageView.setImage(new Image(new FileInputStream(selectedFile)));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  // Метод для очищення полів вводу
  private void clearFields() {
    nameField.clear();
    descriptionArea.clear();
    manufacturerField.clear();
    formField.clear();
    purposeField.clear();
    imageView.setImage(new Image(getClass().getResourceAsStream("/data/icon.png")));
    selectedImageBytes = null;
    categoryComboBox.getCheckModel().clearChecks();
  }

  @Override
  public void updateLanguage() {
    bundle = LanguageManager.getBundle(); // Оновлення ResourceBundle
    clearFieldsButton.setText(bundle.getString("button.clearFields"));
    addButton.setText(bundle.getString("button.add"));
    deleteButton.setText(bundle.getString("button.delete"));
    editButton.setText(bundle.getString("button.edit"));
    nameColumn.setText(bundle.getString("column.name"));
    descriptionColumn.setText(bundle.getString("column.description"));
    manufacturerColumn.setText(bundle.getString("column.manufacturer"));
    formColumn.setText(bundle.getString("column.form"));
    purposeColumn.setText(bundle.getString("column.purpose"));
    categoryLabel.setText(bundle.getString("label.category"));
    chooseButton.setText(bundle.getString("button.choose"));
    descriptionLabel.setText(bundle.getString("label.description"));
    formLabel.setText(bundle.getString("label.form"));
    manufacturerLabel.setText(bundle.getString("label.manufacturer"));
    nameLabel.setText(bundle.getString("label.name"));
    photoLabel.setText(bundle.getString("label.photo"));
    purposeLabel.setText(bundle.getString("label.purpose"));
  }

  private String getLocalizedMessage(String key) {
    return bundle.getString(key);
  }
}
