package com.kudelych.medicalguide.persistence.repository.contract;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import java.util.List;

public interface MedicinesRepository {

  // Додати новий медикамент
  int addMedicine(Medicine medicine);

  // Оновити існуючий медикамент
  void updateMedicine(Medicine medicine) throws EntityNotFoundException;

  // Видалити медикамент за його ідентифікатором
  void deleteMedicine(int id) throws EntityNotFoundException;

  // Знайти всі медикаменти
  List<Medicine> findAll();

  // Знайти категорії за ідентифікатором медикаменту
  List<Category> getCategoriesByMedicineId(int medicineId);

  // Додати категорію до медикаменту
  void addCategoryToMedicine(int medicineId, int categoryId) throws EntityNotFoundException;

  // Видалити категорію з медикаменту
  void removeCategoryFromMedicine(int medicineId, int categoryId) throws EntityNotFoundException;

  // Зберегти медикамент для користувача
  void addSavedMedicine(int userId, int medicineId);

  // Знайти збережені медикаменти для користувача
  List<Medicine> findSavedMedicinesByUserId(int userId);

  // Видалити збережений медикамент для користувача
  void removeSavedMedicine(int userId, int medicineId) throws EntityNotFoundException;
}
