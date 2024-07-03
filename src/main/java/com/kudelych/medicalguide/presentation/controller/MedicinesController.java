package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
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

public class MedicinesController {

  @FXML
  private GridPane medicinesGridPane; // Панель, на якій розміщуються карточки ліків
  @FXML
  private Label medicineName; // Назва лікарського засобу
  @FXML
  private Label medicineDescription; // Опис лікарського засобу
  @FXML
  private Label medicineManufacturer; // Виробник лікарського засобу
  @FXML
  private Label medicineForm; // Форма лікарського засобу
  @FXML
  private Label medicinePurpose; // Призначення лікарського засобу

  private MedicinesRepositoryImpl medicinesRepository; // Репозиторій для отримання списку ліків

  public MedicinesController() {
    // Ініціалізація репозиторію ліків з підключенням до бази даних
    this.medicinesRepository = new MedicinesRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  public void initialize() {
    // Під час ініціалізації контролера завантажуємо список ліків і відображаємо їх карточки
    List<Medicine> medicines = medicinesRepository.findAll();
    displayMedicineCards(medicines);
  }

  private void displayMedicineCards(List<Medicine> medicines) {
    int column = 0; // Змінна для відстеження поточного стовпця
    int row = 0; // Змінна для відстеження поточного рядка
    int cardsPerRow = 3; // Кількість карточок у одному рядку

    medicinesGridPane.getColumnConstraints().clear(); // Очищення старих стовпців
    medicinesGridPane.getRowConstraints().clear(); // Очищення старих рядків

    // Додаємо обмеження для стовпців та рядків
    for (int i = 0; i < cardsPerRow; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints();
      columnConstraints.setPercentWidth(100.0 / cardsPerRow);
      medicinesGridPane.getColumnConstraints().add(columnConstraints);
    }

    for (int i = 0; i < (int) Math.ceil((double) medicines.size() / cardsPerRow); i++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(200); // Мінімальна висота рядка
      medicinesGridPane.getRowConstraints().add(rowConstraints);
    }

    for (Medicine medicine : medicines) {
      // Завантажуємо карточку лікарського засобу
      AnchorPane card = loadMedicineCard(medicine);
      if (card != null) {
        // Додаємо обробник подій для натискання на карточку
        card.setOnMouseClicked(event -> displayMedicineDetails(medicine));

        // Додаємо карточку до GridPane з використанням відповідних індексів стовпця і рядка
        medicinesGridPane.add(card, column, row);

        // Інкрементуємо стовпець
        column++;
        if (column == cardsPerRow) { // Якщо досягнуто кінця рядка, переходимо до наступного
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
      // Завантажуємо FXML-файл карточки лікарського засобу
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/medicine_card.fxml"));
      AnchorPane card = loader.load(); // Завантажуємо карточку
      MedicineCardController controller = loader.getController(); // Отримуємо контролер карточки
      if (controller != null) {
        controller.setMedicine(medicine); // Встановлюємо дані лікарського засобу у контролері
        return card; // Повертаємо завантажену карточку
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

  private void displayMedicineDetails(Medicine medicine) {
    // Відображення деталей лікарського засобу у боковій панелі
    medicineName.setText(medicine.name());
    medicineDescription.setText(medicine.description());
    medicineManufacturer.setText(medicine.manufacturer());
    medicineForm.setText(medicine.form());
    medicinePurpose.setText(medicine.purpose());
  }
}
