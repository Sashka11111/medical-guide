package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.AuthenticatedUser;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;

import java.io.IOException;
import java.util.List;

public class SavedMedicinesController {

  @FXML
  private GridPane savedMedicinesGridPane;
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
  private Label errorLabel;
  private MedicinesRepositoryImpl medicinesRepository;
  private Medicine selectedMedicine;

  public SavedMedicinesController() {
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    loadSavedMedicines();
  }

  private void loadSavedMedicines() {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    List<Medicine> savedMedicines = medicinesRepository.findSavedMedicinesByUserId(currentUser.id());
    displaySavedMedicineCards(savedMedicines);
  }

  private void displaySavedMedicineCards(List<Medicine> medicines) {
    savedMedicinesGridPane.getChildren().clear();
    if (medicines.isEmpty()) {
      errorLabel.setText("Поки у Вас немає збережених ліків");
      return;
    }
    int column = 0;
    int row = 0;
    int cardsPerRow = 3;

    savedMedicinesGridPane.getColumnConstraints().clear();
    savedMedicinesGridPane.getRowConstraints().clear();

    for (int i = 0; i < cardsPerRow; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / cardsPerRow);
      savedMedicinesGridPane.getColumnConstraints().add(columnConstraints);
    }

    for (int i = 0; i < (int) Math.ceil((double) medicines.size() / cardsPerRow); i++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(200);
      savedMedicinesGridPane.getRowConstraints().add(rowConstraints);
    }

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
        System.err.println("Error loading card for medicine: " + medicine.name());
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
        return card;
      } else {
        System.err.println("MedicineCardController is null");
        return null;
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error loading medicineCard.fxml");
      return null;
    }
  }

  @FXML
  private void handleDeleteAction() {
    if (selectedMedicine != null) {
      deleteMedicine(selectedMedicine);
    } else {
      AlertController.showAlert("Видалення лікарського засобу", "Ви не вибрали лікарський засіб.");
    }
  }

  public void deleteMedicine(Medicine medicine) {
    User currentUser = AuthenticatedUser.getInstance().getCurrentUser();
    try {
      medicinesRepository.removeSavedMedicine(currentUser.id(), medicine.id());
      AlertController.showAlert("Видалення лікарського засобу", "Лікарський засіб: "+ medicine.name() + " успішно видалено");
      loadSavedMedicines();
      // Очищуємо інформаційні поля
      clearMedicineDetails();
    } catch (EntityNotFoundException e) {
      AlertController.showAlert("Помилка видалення", "Не вдалося знайти лікарський засіб для видалення.");
    }
  }

  private void clearMedicineDetails() {
    selectedMedicine = null;
    medicineName.setText("");
    medicineDescription.setText("");
    medicineManufacturer.setText("");
    medicineForm.setText("");
    medicinePurpose.setText("");
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
