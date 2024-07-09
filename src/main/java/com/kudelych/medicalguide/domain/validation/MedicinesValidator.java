package com.kudelych.medicalguide.domain.validation;

import com.kudelych.medicalguide.persistence.entity.Medicine;

public class MedicinesValidator {

  // Максимальна довжина для назви лікарського засобу
  private static final int MAX_NAME_LENGTH = 100;

  // Максимальна довжина для назви виробника
  private static final int MAX_MANUFACTURER_LENGTH = 200;

  // Максимальна довжина для форми лікарського засобу
  private static final int MAX_FORM_LENGTH = 100;

  // Максимальна довжина для призначення лікарського засобу
  private static final int MAX_PURPOSE_LENGTH = 200;

  // Максимальна довжина для опису лікарського засобу
  private static final int MAX_DESCRIPTION_LENGTH = 255;

  /**
   * Метод для валідації об'єкта Medicine.
   *
   * @param medicine об'єкт Medicine для валідації
   * @return true, якщо всі поля об'єкта валідні, інакше false
   */
  public static boolean validateMedicine(Medicine medicine) {
    return validateName(medicine.name()) &&
        validateDescription(medicine.description()) &&
        validateManufacturer(medicine.manufacturer()) &&
        validateForm(medicine.form()) &&
        validatePurpose(medicine.purpose()) &&
        validateImage(medicine.image());
  }

  /**
   * Метод для валідації назви лікарського засобу.
   *
   * @param name назва лікарського засобу
   * @return true, якщо назва валідна, інакше false
   */
  public static boolean validateName(String name) {
    return name != null && name.length() <= MAX_NAME_LENGTH;
  }

  /**
   * Метод для валідації опису лікарського засобу.
   *
   * @param description опис лікарського засобу
   * @return true, якщо опис валідний, інакше false
   */
  public static boolean validateDescription(String description) {
    return description != null && description.length() <= MAX_DESCRIPTION_LENGTH;
  }

  /**
   * Метод для валідації назви виробника.
   *
   * @param manufacturer назва виробника
   * @return true, якщо назва виробника валідна, інакше false
   */
  public static boolean validateManufacturer(String manufacturer) {
    return manufacturer != null && manufacturer.length() <= MAX_MANUFACTURER_LENGTH;
  }

  /**
   * Метод для валідації форми лікарського засобу.
   *
   * @param form форма лікарського засобу
   * @return true, якщо форма валідна, інакше false
   */
  public static boolean validateForm(String form) {
    return form != null && form.length() <= MAX_FORM_LENGTH;
  }

  /**
   * Метод для валідації призначення лікарського засобу.
   *
   * @param purpose призначення лікарського засобу
   * @return true, якщо призначення валідне, інакше false
   */
  public static boolean validatePurpose(String purpose) {
    return purpose != null && purpose.length() <= MAX_PURPOSE_LENGTH;
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
