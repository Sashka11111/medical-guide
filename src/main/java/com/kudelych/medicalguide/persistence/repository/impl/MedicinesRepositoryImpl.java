package com.kudelych.medicalguide.persistence.repository.impl;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
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

  @Override
  public void addMedicine(Medicine medicine) {
    String query = "INSERT INTO Medicines (name, description, manufacturer, form, purpose, image) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, medicine.name());
      preparedStatement.setString(2, medicine.description());
      preparedStatement.setString(3, medicine.manufacturer());
      preparedStatement.setString(4, medicine.form());
      preparedStatement.setString(5, medicine.purpose());
      preparedStatement.setBytes(6, medicine.image());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

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

  @Override
  public Medicine findById(int id) throws EntityNotFoundException {
    String query = "SELECT * FROM Medicines WHERE medicine_id = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setInt(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapMedicine(resultSet);
        } else {
          throw new EntityNotFoundException("Медикамент з ідентифікатором " + id + " не знайдено");
        }
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Помилка під час отримання медикаменту з ідентифікатором: " + id, e);
    }
  }

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
