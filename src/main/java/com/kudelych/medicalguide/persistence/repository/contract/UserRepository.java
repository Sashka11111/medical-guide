package com.kudelych.medicalguide.persistence.repository.contract;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.User;
import com.kudelych.medicalguide.persistence.entity.UserRole;
import java.util.List;

public interface UserRepository {
  List<User> findAll();
  void addUser(User user);
  User findByUsername(String username) throws EntityNotFoundException;
  boolean isUsernameExists(String username);
  void updateUserRole(String username, UserRole newRole) throws EntityNotFoundException;
  void deleteUser(String username) throws EntityNotFoundException;
}