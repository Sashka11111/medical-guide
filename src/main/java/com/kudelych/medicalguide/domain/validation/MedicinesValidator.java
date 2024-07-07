package com.kudelych.medicalguide.domain.validation;

import com.kudelych.medicalguide.persistence.entity.Medicine;
import java.util.regex.Pattern;

public class MedicinesValidator {

  // Регулярний вираз для валідації назви лікарського засобу
  private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9 ]{1,50}$");

  // Регулярний вираз для валідації назви виробника
  private static final Pattern MANUFACTURER_PATTERN = Pattern.compile("^[A-Za-z0-9 ]{1,50}$");

  // Регулярний вираз для валідації форми
  private static final Pattern FORM_PATTERN = Pattern.compile("^[A-Za-z ]{1,30}$");

  // Регулярний вираз для валідації призначення
  private static final Pattern PURPOSE_PATTERN = Pattern.compile("^[A-Za-z0-9 ]{1,100}$");

  // Максимальна довжина для опису
  private static final int MAX_DESCRIPTION_LENGTH = 500;

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
        validatePurpose(medicine.purpose());
  }

  /**
   * Метод для валідації назви лікарського засобу.
   *
   * @param name назва лікарського засобу
   * @return true, якщо назва валідна, інакше false
   */
  public static boolean validateName(String name) {
    return name != null && NAME_PATTERN.matcher(name).matches();
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
    return manufacturer != null && MANUFACTURER_PATTERN.matcher(manufacturer).matches();
  }

  /**
   * Метод для валідації форми лікарського засобу.
   *
   * @param form форма лікарського засобу
   * @return true, якщо форма валідна, інакше false
   */
  public static boolean validateForm(String form) {
    return form != null && FORM_PATTERN.matcher(form).matches();
  }

  /**
   * Метод для валідації призначення лікарського засобу.
   *
   * @param purpose призначення лікарського засобу
   * @return true, якщо призначення валідне, інакше false
   */
  public static boolean validatePurpose(String purpose) {
    return purpose != null && PURPOSE_PATTERN.matcher(purpose).matches();
  }
}
