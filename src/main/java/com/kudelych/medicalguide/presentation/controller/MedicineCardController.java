package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.persistence.entity.Medicine;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;

public class MedicineCardController {

  @FXML
  private ImageView medicineImage;

  @FXML
  private Label medicineName;

  private MedicinesController parentController;

  public void setMedicine(Medicine medicine) {
    medicineName.setText(medicine.name());

    if (medicine.image() != null && medicine.image().length > 0) {
      ByteArrayInputStream bis = new ByteArrayInputStream(medicine.image());
      medicineImage.setImage(new Image(bis));
    } else {
      // Встановити зображення за замовчуванням, якщо зображення не надано
      medicineImage.setImage(new Image(getClass().getResourceAsStream("/data/icon.png")));
    }
  }
  public void setParentController(MedicinesController parentController) {
    this.parentController = parentController;
  }
}
