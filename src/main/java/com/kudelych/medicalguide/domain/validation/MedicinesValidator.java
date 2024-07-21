package com.kudelych.medicalguide.domain.validation;

import com.kudelych.medicalguide.persistence.entity.Medicine;
import java.util.List;

public class MedicinesValidator {

  /**
   * Метод для валідації об'єкта Medicine.
   *
   * @param medicine об'єкт Medicine для валідації
   * @return true, якщо всі поля об'єкта валідні, інакше false
   */
  public static boolean validateMedicine(Medicine medicine, List<Medicine> existingMedicines) {
    return validateName(medicine.name()) &&
        validateDescription(medicine.description()) &&
        validateManufacturer(medicine.manufacturer()) &&
        validateForm(medicine.form()) &&
        validatePurpose(medicine.purpose()) &&
        !isMedicineNameDuplicate(medicine.name(), existingMedicines) &&
        validateImage(medicine.image());
  }

  /**
   * Метод для валідації назви лікарського засобу.
   *
   * @param name назва лікарського засобу
   * @return true, якщо назва валідна, інакше false
   */
  public static boolean validateName(String name) {
    return name != null;
  }

  /**
   * Метод для валідації опису лікарського засобу.
   *
   * @param description опис лікарського засобу
   * @return true, якщо опис валідний, інакше false
   */
  public static boolean validateDescription(String description) {
    return description != null;
  }

  /**
   * Метод для валідації назви виробника.
   *
   * @param manufacturer назва виробника
   * @return true, якщо назва виробника валідна, інакше false
   */
  public static boolean validateManufacturer(String manufacturer) {
    return manufacturer != null;
  }

  /**
   * Метод для валідації форми лікарського засобу.
   *
   * @param form форма лікарського засобу
   * @return true, якщо форма валідна, інакше false
   */
  public static boolean validateForm(String form) {
    return form != null;
  }

  /**
   * Метод для валідації призначення лікарського засобу.
   *
   * @param purpose призначення лікарського засобу
   * @return true, якщо призначення валідне, інакше false
   */
  public static boolean validatePurpose(String purpose) {
    return purpose != null ;
  }
  // Метод для перевірки наявності лікарського засобу з заданою назвою
  public static boolean isMedicineNameDuplicate(String name, List<Medicine> existingMedicines) {
    return existingMedicines.stream().anyMatch(medicine -> medicine.name().equalsIgnoreCase(name));
  }
  /**
   * Метод для валідації зображення лікарського засобу.
   *
   * @param image зображення лікарського засобу
   * @return true, якщо зображення є null або що зображення не пусте
   */
  public static boolean validateImage(byte[] image) {
    return image == null || image.length > 0;
  }
}
