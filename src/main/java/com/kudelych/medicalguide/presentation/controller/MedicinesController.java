package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.repository.impl.MedicinesRepositoryImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class MedicinesController {

  @FXML
  private GridPane medicinesGridPane; // Панель, на якій розміщуються карточки ліків

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

    double cardWidth = 300.0; // Ширина карточки лікарського засобу
    double cardHeight = 200.0; // Висота карточки лікарського засобу
    double horizontalGap = 20.0; // Горизонтальний відступ між карточками
    double verticalGap = 20.0; // Вертикальний відступ між карточками

    for (Medicine medicine : medicines) {
      // Завантажуємо карточку лікарського засобу
      AnchorPane card = loadMedicineCard(medicine);
      if (card != null) {
        // Додаємо карточку до GridPane з використанням відповідних індексів стовпця і рядка
        medicinesGridPane.add(card, column, row);

        // Інкрементуємо стовпець
        column++;

        // Перевіряємо, чи досягнули максимальної кількості стовпців
        if (column == 2) { // Наприклад, 2 карточки в одному рядку
          column = 0; // Скидаємо стовпець до початкового значення
          row++; // Інкрементуємо рядок
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
}
