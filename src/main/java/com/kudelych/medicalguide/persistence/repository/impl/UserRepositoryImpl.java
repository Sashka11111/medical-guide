package com.kudelych.medicalguide.persistence.repository.impl;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import com.kudelych.medicalguide.persistence.repository.contract.UserRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class UserRepositoryImpl implements UserRepository {

  private DataSource dataSource;

  public UserRepositoryImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  @Override
  public List<User> findAll() {
    List<User> users = new ArrayList<>();
    String query = "SELECT * FROM Users";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery()) {
      while (resultSet.next()) {
        User user = mapUser(resultSet);
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
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

  public void updateUserRole(String username, UserRole newRole) throws EntityNotFoundException {
    String query = "UPDATE Users SET role = ? WHERE username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, newRole.toString());
      preparedStatement.setString(2, username);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Користувача з ім'ям " + username + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void deleteUser(String username) throws EntityNotFoundException {
    String query = "DELETE FROM Users WHERE username = ?";
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.setString(1, username);
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new EntityNotFoundException("Користувача з ім'ям " + username + " не знайдено");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
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
