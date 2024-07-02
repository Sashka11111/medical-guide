package com.kudelych.medicalguide.presentation.viewmodel;

import com.kudelych.medicalguide.persistence.entity.Category;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class CategoryViewModel {
  private final SimpleIntegerProperty idCategory;
  private final SimpleStringProperty nameCategory;

  public CategoryViewModel(Category category) {
    this.idCategory = new SimpleIntegerProperty(category.id());
    this.nameCategory = new SimpleStringProperty(category.name());
  }

  public int getIdCategory() {
    return idCategory.get();
  }

  public SimpleIntegerProperty idCategoryProperty() {
    return idCategory;
  }

  public void setIdCategory(int idCategory) {
    this.idCategory.set(idCategory);
  }

  public String getNameCategory() {
    return nameCategory.get();
  }

  public SimpleStringProperty nameCategoryProperty() {
    return nameCategory;
  }

  public void setNameCategory(String nameCategory) {
    this.nameCategory.set(nameCategory);
  }
}
