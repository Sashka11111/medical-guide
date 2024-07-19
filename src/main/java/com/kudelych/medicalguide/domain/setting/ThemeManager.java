package com.kudelych.medicalguide.domain.setting;

public class ThemeManager {
  private static String currentTheme;

  public static String getCurrentTheme() {
    return currentTheme;
  }

  public static void setCurrentTheme(String theme) {
    currentTheme = theme;
  }
}