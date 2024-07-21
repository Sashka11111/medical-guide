package com.kudelych.medicalguide.presentation.controller;

import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.setting.ControllerManager;
import com.kudelych.medicalguide.domain.setting.LanguageManager;
import com.kudelych.medicalguide.domain.setting.LanguageUpdatable;
import com.kudelych.medicalguide.domain.validation.CategoryValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.contract.CategoryRepository;
import com.kudelych.medicalguide.persistence.repository.impl.CategoryRepositoryImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.ResourceBundle;

public class CategoryController implements LanguageUpdatable {

  @FXML
  private TableColumn<Category, Integer> Category_col_IdCategory;

  @FXML
  private TableColumn<Category, String> Category_col_NameCategory;

  @FXML
  private TableView<Category> Category_tableView;

  @FXML
  private TextField addCategory;

  @FXML
  private Label categoryLabel;

  @FXML
  private Button btn_add;

  @FXML
  private Button btn_clear;

  @FXML
  private Button btn_delete;

  @FXML
  private Button btn_edit;

  private final CategoryRepository categoryRepository;

  public CategoryController() {
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    ControllerManager.registerController(this);
    ControllerManager.notifyAllControllers();
    Category_col_IdCategory.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id()).asObject());
    Category_col_NameCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().name()));
    loadCategories();
    Category_tableView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            addCategory.setText(newValue.name());
          }
        }
    );
    btn_add.setOnAction(event -> onAddClicked());
    btn_clear.setOnAction(event -> onClearClicked());
    btn_delete.setOnAction(event -> onDeleteClicked());
    btn_edit.setOnAction(event -> onEditClicked());
  }

  private void loadCategories() {
    List<Category> categories = categoryRepository.getAllCategories();
    ObservableList<Category> categoryViewModels = FXCollections.observableArrayList();
    categoryViewModels.setAll(categories);
    Category_tableView.setItems(categoryViewModels);
  }

  private void onAddClicked() {
    String categoryName = addCategory.getText().trim();
    List<Category> existingCategories = categoryRepository.getAllCategories();
    String validationMessage = CategoryValidator.validateCategoryName(categoryName, existingCategories, LanguageManager.getCurrentLocale());
    if (validationMessage == null) {
      Category newCategory = new Category(0, categoryName);
      try {
        categoryRepository.addCategory(newCategory);
        loadCategories();
        addCategory.clear();
      } catch (EntityNotFoundException e) {
        AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), LanguageManager.getBundle().getString("error.addingCategory"));
        e.printStackTrace();
      }
    } else {
      AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), validationMessage);
    }
  }

  private void onClearClicked() {
    addCategory.clear();
  }

  private void onDeleteClicked() {
    Category selectedCategory = Category_tableView.getSelectionModel().getSelectedItem();
    if (selectedCategory != null) {
      try {
        int categoryId = selectedCategory.id();
        List<Integer> byCategoryId = categoryRepository.getMedicineByCategoryId(categoryId);
        if (byCategoryId.isEmpty()) {
          categoryRepository.deleteCategory(categoryId);
          loadCategories();
          onClearClicked();
        } else {
          AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), LanguageManager.getBundle().getString("error.deleteCategoryHasItems"));
        }
      } catch (EntityNotFoundException e) {
        AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), LanguageManager.getBundle().getString("error.deletingCategory"));
        e.printStackTrace();
      }
    }
  }

  private void onEditClicked() {
    Category selectedCategory = Category_tableView.getSelectionModel().getSelectedItem();
    if (selectedCategory != null) {
      String newName = addCategory.getText().trim();
      List<Category> existingCategories = categoryRepository.getAllCategories();
      String validationMessage = CategoryValidator.validateCategoryName(newName, existingCategories, LanguageManager.getCurrentLocale());
      if (validationMessage == null) {
        try {
          Category updatedCategory = new Category(selectedCategory.id(), newName);
          categoryRepository.updateCategory(updatedCategory);
          loadCategories();
          addCategory.clear();
        } catch (EntityNotFoundException e) {
          AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), LanguageManager.getBundle().getString("error.updatingCategory"));
          e.printStackTrace();
        }
      } else {
        AlertController.showAlert(LanguageManager.getBundle().getString("error.title"), validationMessage);
      }
    }
  }

  @Override
  public void updateLanguage() {
    ResourceBundle bundle = LanguageManager.getBundle();
    categoryLabel.setText(bundle.getString("label.category"));
    btn_add.setText(bundle.getString("button.add"));
    btn_delete.setText(bundle.getString("button.delete"));
    btn_edit.setText(bundle.getString("button.edit"));
    Category_col_IdCategory.setText(bundle.getString("column.id"));
    Category_col_NameCategory.setText(bundle.getString("column.name"));
  }
}
