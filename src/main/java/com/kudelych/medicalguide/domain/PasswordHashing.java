package com.kudelych.medicalguide.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Клас для хешування паролю за допомогою алгоритму SHA-256.
 */
public class PasswordHashing {

  private static PasswordHashing instance;

  /**
   * Приватний конструктор класу.
   */
  private PasswordHashing() {
  }
  /**
   * Метод, який повертає єдиний екземпляр класу PasswordHashing.
   *
   * @return єдиний екземпляр класу PasswordHashing
   */
  public static PasswordHashing getInstance() {
    if (instance == null) {
      instance = new PasswordHashing();
    }
    return instance;
  }
  /**
   * Метод для генерації хешу пароля.
   *
   * @param password пароль, який потрібно захешувати
   * @return хешоване значення пароля
   */
  public String hashedPassword(String password) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = messageDigest.digest(password.getBytes());

      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedHash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return null;
  }
}
