package com.kudelych.medicalguide.persistence.repository.impl;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import com.kudelych.medicalguide.persistence.repository.contract.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

public class UserRepositoryImpl implements UserRepository {

  private DataSource dataSource;

  public UserRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void addUser(User user) {
    String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, user.username());
      preparedStatement.setString(2, user.password());
      preparedStatement.setString(3, user.role().toString());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public User findByUsername(String username) throws EntityNotFoundException {
    String query = "SELECT * FROM Users WHERE username = ?";
    User user = null;
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          user = mapUser(resultSet);
        } else {
          throw new EntityNotFoundException("Користувача з ім'ям " + username + " не знайдено");
        }
      }
    } catch (SQLException e) {
      throw new EntityNotFoundException("Помилка під час отримання користувача з іменем: " + username, e);
    }
    return user;
  }

  @Override
  public boolean isUsernameExists(String username) {
    String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          int count = resultSet.getInt(1);
          return count > 0;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  private User mapUser(ResultSet resultSet) throws SQLException {
    int id = resultSet.getInt("user_id");
    String username = resultSet.getString("username");
    String password = resultSet.getString("password");
    String roleStr = resultSet.getString("role");
    UserRole role = UserRole.valueOf(roleStr); // Конвертація рядка в перерахування UserRole
    return new User(id, username, password, role);
  }
}
