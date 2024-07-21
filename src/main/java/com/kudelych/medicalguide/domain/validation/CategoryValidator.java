package com.kudelych.medicalguide.domain.validation;

import com.kudelych.medicalguide.persistence.entity.Category;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CategoryValidator {

  // Метод для перевірки назви категорії з урахуванням мови
  public static String validateCategoryName(String name, List<Category> existingCategories, Locale locale) {
    ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
    if (name == null || name.trim().isEmpty()) {
      return bundle.getString("error.categoryNameEmpty");
    } else if (name.length() > 50) {
      return bundle.getString("error.categoryNameTooLong");
    } else if (isCategoryNameDuplicate(name, existingCategories)) {
      return bundle.getString("error.categoryNameDuplicate");
    } else {
      return null; // Якщо назва валідна, повертаємо null
    }
  }

  // Метод для перевірки наявності категорії з заданою назвою
  private static boolean isCategoryNameDuplicate(String name, List<Category> existingCategories) {
    return existingCategories.stream().anyMatch(category -> category.name().equalsIgnoreCase(name));
  }
}
