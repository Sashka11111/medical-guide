package com.kudelych.medicalguide.domain.setting;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleManager {

  private static ResourceBundle bundle = ResourceBundle.getBundle("messages_uk", new Locale("uk", "UA"));

  public static ResourceBundle getBundle() {
    return bundle;
  }

  public static void setBundle(Locale locale) {
    bundle = ResourceBundle.getBundle("messages", locale);
  }
}
