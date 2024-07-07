package com.kudelych.medicalguide.persistence.repository.contract;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.Category;
import java.util.List;

public interface CategoryRepository {

  void addCategory(Category category) throws EntityNotFoundException;

  List<Category> getAllCategories();

  void updateCategory(Category category) throws EntityNotFoundException;

  void deleteCategory(int id) throws EntityNotFoundException;

  // Методи для звязку багато до багатьох
  void addCategoryToMedicine(int categoryId, int medicineId);

  void removeCategoryFromMedicine(int categoryId, int medicineId) throws EntityNotFoundException;

  List<Integer> getCategoriesByMedicineId(int medicineId);

  List<Integer> getMedicineByCategoryId(int categoryId);
}
