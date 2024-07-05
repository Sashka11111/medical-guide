package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.impl.CategoryRepositoryImpl;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import java.io.IOException;
import java.util.Arrays;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.controlsfx.control.CheckComboBox;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
    descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().description()));
    manufacturerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().manufacturer()));
    formColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().form()));
    purposeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().purpose()));

    loadMedicines();
    loadCategories();

    medicineTable.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> showMedicineDetails(newValue));
    clearFieldsButton.setOnAction(event -> clearFields());
  }

  private void loadMedicines() {
    medicineData.setAll(medicinesRepository.findAll());
    medicineTable.setItems(medicineData);
  }

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

  private void showMedicineDetails(Medicine medicine) {
    if (medicine != null) {
      nameField.setText(medicine.name());
      descriptionArea.setText(medicine.description());
      manufacturerField.setText(medicine.manufacturer());
      formField.setText(medicine.form());
      purposeField.setText(medicine.purpose());
      // Set selected categories
      categoryComboBox.getCheckModel().clearChecks();
      List<Category> medicineCategories = medicinesRepository.getCategoriesByMedicineId(medicine.id());
      for (Category category : medicineCategories) {
        categoryComboBox.getCheckModel().check(category);
      }
    } else {
      clearFields();
    }
  }

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
    List<Category> selectedCategories = categoryComboBox.getCheckModel().getCheckedItems();
    try {
      // Додаємо лік до репозиторію і отримуємо його ID
      int newMedicineId = medicinesRepository.addMedicine(newMedicine);

      // Перевіряємо, чи вдалося додати лік і отримати його ID
      if (newMedicineId != -1) {
        // Додаємо вибрані категорії до ліку за отриманим ID
        for (Category category : selectedCategories) {
          medicinesRepository.addCategoryToMedicine(newMedicineId, category.id());
        }
      }

      // Оновлюємо таблицю ліків та очищаємо поля введення
      loadMedicines();
      clearFields();

    } catch (EntityNotFoundException e) {
      throw new RuntimeException(e);
    }
  }


  @FXML
  private void handleEditAction() {
    Medicine selectedMedicine = medicineTable.getSelectionModel().getSelectedItem();
    if (selectedMedicine != null) {
      selectedMedicine.name();
      selectedMedicine.description();
      selectedMedicine.manufacturer();
      selectedMedicine.form();
      selectedMedicine.purpose();
      selectedMedicine.image();

      try {
        medicinesRepository.updateMedicine(selectedMedicine);
        List<Integer> categoryIds = categoryRepository.getCategoriesByMedicineId(selectedMedicine.id());
        for (int categoryId : categoryIds) {
          categoryRepository.removeCategoryFromMedicine(categoryId, selectedMedicine.id());
        }
        for (Category category : categoryComboBox.getCheckModel().getCheckedItems()) {
          categoryRepository.addCategoryToMedicine(selectedMedicine.id(), category.id());
        }
        loadMedicines();
      } catch (EntityNotFoundException e) {
        showAlert("Error", "Medicine not found!");
      }
    } else {
      showAlert("Error", "No medicine selected for editing.");
    }
  }

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
        showAlert("Error", "Medicine not found!");
      }
    } else {
      showAlert("Error", "No medicine selected for deletion.");
    }
  }

  @FXML
  private void handleSelectImageAction() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
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

  private void showAlert(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
