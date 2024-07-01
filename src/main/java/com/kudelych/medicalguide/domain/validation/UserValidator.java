package com.kudelych.medicalguide.domain.validation;

import java.util.regex.Pattern;

public class UserValidator {
  // Мінімальна довжина паролю
  private static final int MIN_PASSWORD_LENGTH = 6;

  // Паттерн для перевірки складності паролю
  private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";

  // Перевірка унікальності імені користувача
  public static boolean isUsernameValid(String username) {
    return username != null && !username.isEmpty();
  }

  // Перевірка паролю
  public static boolean isPasswordValid(String password) {
    return password != null && password.length() >= MIN_PASSWORD_LENGTH && Pattern.matches(PASSWORD_PATTERN, password);
  }
}