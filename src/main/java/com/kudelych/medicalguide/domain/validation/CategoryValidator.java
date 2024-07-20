package com.kudelych.medicalguide.domain.validation;

import com.kudelych.medicalguide.persistence.entity.Category;
import java.util.List;

public class CategoryValidator {

  // Метод для перевірки назви категорії
  public static String validateCategoryName(String name, List<Category> existingCategories) {
    if (name == null || name.trim().isEmpty()) {
      return "error.categoryNameEmpty";
    } else if (name.length() > 50) {
      return "error.categoryNameTooLong";
    } else if (isCategoryNameDuplicate(name, existingCategories)) {
      return "error.categoryNameDuplicate";
    } else {
      return null; // Якщо назва валідна, повертаємо null
    }
  }

  // Метод для перевірки наявності категорії з заданою назвою
  private static boolean isCategoryNameDuplicate(String name, List<Category> existingCategories) {
    return existingCategories.stream().anyMatch(category -> category.name().equalsIgnoreCase(name));
  }
}
