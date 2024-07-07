package com.kudelych.medicalguide.persistence.repository.impl;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.contract.CategoryRepository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl implements CategoryRepository {

  private DataSource dataSource;

  // Конструктор з джерелом даних
  public CategoryRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void addCategory(Category category) throws EntityNotFoundException {
    String sql = "INSERT INTO Categories(category_name) VALUES (?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      preparedStatement.setString(1, category.name());
      preparedStatement.executeUpdate();
      ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
      if (generatedKeys.next()) {
        category.id();
      } else {
        throw new SQLException("Не вдалося додати категорію, не отримано ідентифікатор.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося додати категорію.", e);
    }
  }

  @Override
  public void updateCategory(Category category) throws EntityNotFoundException {
    String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, category.name());
      preparedStatement.setInt(2, category.id());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Не вдалося оновити категорію, категорію не знайдено.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося оновити категорію.", e);
    }
  }

  @Override
  public void deleteCategory(int categoryId) throws EntityNotFoundException {
    String sql = "DELETE FROM Categories WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Не вдалося видалити категорію, категорію не знайдено.");
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Не вдалося видалити категорію.", e);
    }
  }

  @Override
  public List<Category> getAllCategories() {
    List<Category> categories = new ArrayList<>();
    String sql = "SELECT * FROM Categories";
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        Category category = new Category(resultSet.getInt("category_id"), resultSet.getString("category_name"));
        categories.add(category);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося отримати всі категорії.", e);
    }
    return categories;
  }

  // Методи для зв'язку багато-до-багатьох

  // Додавання категорії до медичного засобу
  public void addCategoryToMedicine(int categoryId, int medicineId) {
    String sql = "INSERT INTO MedicineCategories (medicine_id, category_id) VALUES (?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      preparedStatement.setInt(2, medicineId);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося додати категорію до медичного засобу.", e);
    }
  }

  // Видалення категорії з медичного засобу
  public void removeCategoryFromMedicine(int categoryId, int medicineId) throws EntityNotFoundException {
    String sql = "DELETE FROM MedicineCategories WHERE category_id = ? AND medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      preparedStatement.setInt(2, medicineId);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Категорію чи медичний засіб не знайдено.");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося видалити категорію з медичного засобу.", e);
    }
  }

  // Отримання списку категорій за ідентифікатором медичного засобу
  public List<Integer> getCategoriesByMedicineId(int medicineId) {
    List<Integer> categories = new ArrayList<>();
    String sql = "SELECT category_id FROM MedicineCategories WHERE medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, medicineId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        categories.add(resultSet.getInt("category_id"));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося отримати категорії за ідентифікатором медичного засобу.", e);
    }
    return categories;
  }

  // Отримання списку медичних засобів за ідентифікатором категорії
  public List<Integer> getMedicineByCategoryId(int categoryId) {
    List<Integer> medicine = new ArrayList<>();
    String sql = "SELECT medicine_id FROM MedicineCategories WHERE category_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setInt(1, categoryId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        medicine.add(resultSet.getInt("medicine_id"));
      }
    } catch (SQLException e) {
      throw new RuntimeException("Не вдалося отримати медичний засіб за ідентифікатором категорії.", e);
    }
    return medicine;
  }
}
