package com.kudelych.medicalguide.domain.setting;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {

  private static final String LANGUAGE_KEY = "currentLanguage";
  private static final Preferences prefs = Preferences.userNodeForPackage(LanguageManager.class);
  private static ResourceBundle bundle;

  static {
    Locale locale = getCurrentLocale();
    bundle = ResourceBundle.getBundle("messages", locale);
  }

  public static ResourceBundle getBundle() {
    return bundle;
  }

  public static void setBundle(Locale locale) {
    bundle = ResourceBundle.getBundle("messages", locale);
    setCurrentLocale(locale);
  }

  public static Locale getCurrentLocale() {
    String languageTag = prefs.get(LANGUAGE_KEY, "uk-UA"); // За замовчуванням українська мова
    return Locale.forLanguageTag(languageTag);
  }

  private static void setCurrentLocale(Locale locale) {
    prefs.put(LANGUAGE_KEY, locale.toLanguageTag());
  }
}
