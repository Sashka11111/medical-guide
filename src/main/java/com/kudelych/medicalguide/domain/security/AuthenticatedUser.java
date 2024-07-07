package com.kudelych.medicalguide.domain.security;

import com.kudelych.medicalguide.persistence.entity.User;

/**
 * Клас AuthenticatedUser представляє єдиний екземпляр поточного аутентифікованого користувача в системі.
 */
public class AuthenticatedUser {
  private static AuthenticatedUser instance; // Єдиний екземпляр класу
  private User currentUser; // Поточний аутентифікований користувач

  private AuthenticatedUser() {} // Приватний конструктор для заборони створення зовнішніми класами

  /**
   * Повертає єдиний екземпляр класу AuthenticatedUser.
   *
   * @return єдиний екземпляр AuthenticatedUser
   */
  public static AuthenticatedUser getInstance() {
    if (instance == null) {
      instance = new AuthenticatedUser();
    }
    return instance;
  }

  /**
   * Повертає поточного аутентифікованого користувача.
   *
   * @return поточний аутентифікований користувач
   */
  public User getCurrentUser() {
    return currentUser;
  }

  /**
   * Встановлює поточного аутентифікованого користувача.
   *
   * @param user користувач, який аутентифікувався
   */
  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
}
