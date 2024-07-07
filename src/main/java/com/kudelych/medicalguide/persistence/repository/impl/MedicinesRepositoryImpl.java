package com.kudelych.medicalguide.persistence.repository.impl;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.entity.Medicine;
import com.kudelych.medicalguide.persistence.repository.contract.MedicinesRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicinesRepositoryImpl implements MedicinesRepository {

  private final DataSource dataSource;

  public MedicinesRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  // Додати медикамент
  @Override
  public int addMedicine(Medicine medicine) {
    String query = "INSERT INTO Medicines (name, description, manufacturer, form, purpose, image) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, medicine.name());
      preparedStatement.setString(2, medicine.description());
      preparedStatement.setString(3, medicine.manufacturer());
      preparedStatement.setString(4, medicine.form());
      preparedStatement.setString(5, medicine.purpose());
      preparedStatement.setBytes(6, medicine.image());
      preparedStatement.executeUpdate();
      try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1);
        } else {
          throw new SQLException("Помилка при створенні, не отримано ID.");
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  // Оновити медикамент
  @Override
  public void updateMedicine(Medicine medicine) throws EntityNotFoundException {
    String query = "UPDATE Medicines SET name = ?, description = ?, manufacturer = ?, form = ?, purpose = ?, image = ? WHERE medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, medicine.name());
      preparedStatement.setString(2, medicine.description());
      preparedStatement.setString(3, medicine.manufacturer());
      preparedStatement.setString(4, medicine.form());
      preparedStatement.setString(5, medicine.purpose());
      preparedStatement.setBytes(6, medicine.image());
      preparedStatement.setInt(7, medicine.id());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Медикамент з ідентифікатором " + medicine.id() + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Видалити медикамент за ідентифікатором
  @Override
  public void deleteMedicine(int id) throws EntityNotFoundException {
    String query = "DELETE FROM Medicines WHERE medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, id);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Медикамент з ідентифікатором " + id + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Знайти всі медикаменти
  @Override
  public List<Medicine> findAll() {
    String query = "SELECT * FROM Medicines";
    List<Medicine> medicines = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        medicines.add(mapMedicine(resultSet));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return medicines;
  }

  // Додати збережений медикамент для користувача
  public void addSavedMedicine(int userId, int medicineId) {
    String query = "INSERT INTO SavedMedicine (user_id, medicine_id) VALUES (?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, medicineId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Знайти всі збережені медикаменти для користувача
  public List<Medicine> findSavedMedicinesByUserId(int userId) {
    String query = "SELECT * FROM Medicines " +
        "JOIN SavedMedicine ON Medicines.medicine_id = SavedMedicine.medicine_id " +
        "WHERE SavedMedicine.user_id = ?";
    List<Medicine> medicines = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, userId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        medicines.add(mapMedicine(resultSet));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return medicines;
  }

  // Видалити збережений медикамент для користувача
  public void removeSavedMedicine(int userId, int medicineId) throws EntityNotFoundException {
    String query = "DELETE FROM SavedMedicine WHERE user_id = ? AND medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, medicineId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Збережений медикамент не знайдено для user_id: " + userId + " та medicine_id: " + medicineId);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Додати категорію до медикаменту
  public void addCategoryToMedicine(int medicineId, int categoryId) throws EntityNotFoundException {
    String query = "INSERT INTO MedicineCategories (medicine_id, category_id) VALUES (?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, medicineId);
      preparedStatement.setInt(2, categoryId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      if (e.getErrorCode() == 19) { // SQLite constraint violation
        throw new EntityNotFoundException("Унікальний constraint порушено для medicine_id: " + medicineId + " та category_id: " + categoryId, e);
      }
      e.printStackTrace();
    }
  }

  // Видалити категорію з медикаменту
  public void removeCategoryFromMedicine(int medicineId, int categoryId) throws EntityNotFoundException {
    String query = "DELETE FROM MedicineCategories WHERE medicine_id = ? AND category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, medicineId);
      preparedStatement.setInt(2, categoryId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Зв'язок між medicine_id: " + medicineId + " та category_id: " + categoryId + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // Отримати категорії за ідентифікатором медикаменту
  public List<Category> getCategoriesByMedicineId(int medicineId) {
    String query = "SELECT * FROM Categories " +
        "JOIN MedicineCategories ON Categories.category_id = MedicineCategories.category_id " +
        "WHERE MedicineCategories.medicine_id = ?";
    List<Category> categories = new ArrayList<>();
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, medicineId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        int id = resultSet.getInt("category_id");
        String name = resultSet.getString("category_name");
        categories.add(new Category(id, name));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return categories;
  }

  // Метод для маппінгу рядка ResultSet на об'єкт Medicine
  private Medicine mapMedicine(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("medicine_id");
    String name = resultSet.getString("name");
    String description = resultSet.getString("description");
    String manufacturer = resultSet.getString("manufacturer");
    String form = resultSet.getString("form");
    String purpose = resultSet.getString("purpose");
    byte[] image = resultSet.getBytes("image");
    return new Medicine(id, name, description, manufacturer, form, purpose, image);
  }
}
