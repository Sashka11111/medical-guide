package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.validation.MedicinesValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.impl.CategoryRepositoryImpl;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
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

public class MedicineManagementController {

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
  private ImageView imageView;

  @FXML
  private CheckComboBox<Category> categoryComboBox;

  private MedicinesRepositoryImpl medicinesRepository;
  private CategoryRepositoryImpl categoryRepository;
  private ObservableList<Medicine> medicineData = FXCollections.observableArrayList();
  private byte[] selectedImageBytes;

  public MedicineManagementController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  private void initialize() {
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
    if (!MedicinesValidator.validateMedicine(newMedicine)) {
      AlertController.showAlert("Помилка", "Будь ласка, заповніть усі поля.");
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
        clearFields();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Повідомлення", "Лікарський засіб успішно редаговано!");
      }
    } else {
      AlertController.showAlert("Помилка", "Не вибрано лікарський засіб для редагування.");
    }
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
        clearFields();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert("Помилка", "Лікарський засіб не знайдено!");
      }
    } else {
      AlertController.showAlert("Помилка", "Не вибрано лікарський засіб для видалення.");
    }
  }

  // Обробник кнопки вибору зображення
  @FXML
  private void handleSelectImageAction() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення", "*.png", "*.jpg", "*.jpeg"));
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
    imageView.setImage(null);
    selectedImageBytes = null;
    categoryComboBox.getCheckModel().clearChecks();
  }
}
