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

  @FXML
  private Label medicineDescription;

  @FXML
  private Label medicineManufacturer;

  @FXML
  private Label medicineForm;

  @FXML
  private Label medicinePurpose;

  public void setMedicine(Medicine medicine) {
    medicineName.setText(medicine.name());
    medicineDescription.setText(medicine.description());
    medicineManufacturer.setText(medicine.manufacturer());
    medicineForm.setText(medicine.form());
    medicinePurpose.setText(medicine.purpose());

    if (medicine.image() != null && medicine.image().length > 0) {
      ByteArrayInputStream bis = new ByteArrayInputStream(medicine.image());
      medicineImage.setImage(new Image(bis));
    } else {
      // Встановити зображення за замовчуванням, якщо зображення не надано
      medicineImage.setImage(new Image(getClass().getResourceAsStream("/data/drugs.png")));
    }
  }
}
