package com.kudelych.medicalguide.persistence.repository.contract;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.persistence.entity.User;

public interface UserRepository {
  void addUser(User user);
  User findByUsername(String username) throws EntityNotFoundException;
  boolean isUsernameExists(String username);
}