package com.kudelych.medicalguide.domain.setting;

import java.util.ArrayList;
import java.util.List;

public class ControllerManager {
  private static final List<LanguageUpdatable> controllers = new ArrayList<>();

  public static void registerController(LanguageUpdatable controller) {
    controllers.add(controller);
  }

  public static void notifyAllControllers() {
    for (LanguageUpdatable controller : controllers) {
      controller.updateLanguage();
    }
  }
}

