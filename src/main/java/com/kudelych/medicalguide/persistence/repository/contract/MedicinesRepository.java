package com.kudelych.medicalguide.persistence.repository.contract;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import java.util.List;

public interface MedicinesRepository {

  // Додати новий медикамент
  int addMedicine(Medicine medicine);

  // Оновити існуючий медикамент
  void updateMedicine(Medicine medicine) throws EntityNotFoundException;

  // Видалити медикамент за його ідентифікатором
  void deleteMedicine(int id) throws EntityNotFoundException;

  // Знайти медикамент за його ідентифікатором
  Medicine findById(int id) throws EntityNotFoundException;

  // Знайти всі медикаменти
  List<Medicine> findAll();
}