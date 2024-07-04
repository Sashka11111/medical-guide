package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.text.Text;

public class MedicinesController {

  @FXML
  private GridPane medicinesGridPane;
  @FXML
  private Label medicineName;
  @FXML
  private Label medicineDescription;
  @FXML
  private Label medicineManufacturer;
  @FXML
  private Label medicineForm;
  @FXML
  private Label medicinePurpose;
  @FXML
  private TextField searchTextField;
  @FXML
  private Button saveMedicineButton;
  private MedicinesRepositoryImpl medicinesRepository;
  private Medicine selectedMedicine;
  private List<Medicine> savedMedicines;

  public MedicinesController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
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
      Text noResults = new Text("На жаль, таких ліків немає");
      medicinesGridPane.add(noResults, 0, 0);
      return;
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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicine_card.fxml"));
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
      System.err.println("Помилка завантаження medicine_card.fxml");
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
    if (selectedMedicine != null) {
      saveMedicine(selectedMedicine);
    } else {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Збереження лікарського засобу");
      alert.setHeaderText(null);
      alert.setContentText("Ви не вибрали лікарський засіб.");
      alert.showAndWait();
    }
  }

  public void saveMedicine(Medicine medicine) {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    if (savedMedicines.stream().anyMatch(savedMedicine -> savedMedicine.id() == medicine.id())) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Збереження лікарського засобу");
      alert.setHeaderText(null);
      alert.setContentText("Цей лікарський засіб вже збережено.");
      alert.showAndWait();
    } else {
      medicinesRepository.addSavedMedicine(currentUser.id(), medicine.id());
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Збереження лікарського засобу");
      alert.setHeaderText(null);
      alert.setContentText("Лікарський засіб збережено: " + medicine.name());
      alert.showAndWait();
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
  }
}
