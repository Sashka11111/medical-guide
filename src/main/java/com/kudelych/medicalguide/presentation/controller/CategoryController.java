package com.kudelych.medicalguide.presentation.controller;


import com.kudelych.medicalguide.domain.exception.EntityNotFoundException;
import com.kudelych.medicalguide.domain.validation.CategoryValidator;
import com.kudelych.medicalguide.persistence.connection.DatabaseConnection;
import com.kudelych.medicalguide.persistence.entity.Category;
import com.kudelych.medicalguide.persistence.repository.contract.CategoryRepository;
import com.kudelych.medicalguide.persistence.repository.impl.CategoryRepositoryImpl;
import com.kudelych.medicalguide.presentation.viewmodel.CategoryViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;

public class CategoryController {

  @FXML
  private TableColumn<CategoryViewModel, Integer> Category_col_IdCategory;

  @FXML
  private TableColumn<CategoryViewModel, String> Category_col_NameCategory;

  @FXML
  private TableView<CategoryViewModel> Category_tableView;

  @FXML
  private TextField addCategory;

  @FXML
  private Button btn_add;

  @FXML
  private Button btn_clear;

  @FXML
  private Button btn_delete;
  @FXML
  private Button btn_edit;
  @FXML
  private Label errorMessage;
  private final CategoryRepository categoryRepository;
  //private final GoalRepository goalRepository;

  public CategoryController() {
    this.categoryRepository = new CategoryRepositoryImpl(new DatabaseConnection().getDataSource());
   // this.goalRepository = new GoalRepositoryImpl(new DatabaseConnection().getDataSource());
  }

  @FXML
  void initialize() {
    Category_col_IdCategory.setCellValueFactory(cellData -> cellData.getValue().idCategoryProperty().asObject());
    Category_col_NameCategory.setCellValueFactory(cellData -> cellData.getValue().nameCategoryProperty());
    loadCategories();
    Category_tableView.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            addCategory.setText(newValue.getNameCategory());
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
    ObservableList<CategoryViewModel> categoryViewModels = FXCollections.observableArrayList();
    categories.forEach(category -> categoryViewModels.add(new CategoryViewModel(category)));
    Category_tableView.setItems(categoryViewModels);
  }

  private void onAddClicked() {
    String categoryName = addCategory.getText().trim();
    List<Category> existingCategories = categoryRepository.getAllCategories();
    String validationMessage = CategoryValidator.validateCategoryName(categoryName, existingCategories);
    if (validationMessage == null) {
      Category newCategory = new Category(0, categoryName);
      try {
        categoryRepository.addCategory(newCategory);
        loadCategories();
        addCategory.clear();
      } catch (EntityNotFoundException e) {
        e.printStackTrace();
      }
    } else {
      errorMessage.setText(validationMessage);
    }
  }

  private void onClearClicked() {
    addCategory.clear();
  }

  private void onDeleteClicked() {
    CategoryViewModel selectedCategoryViewModel = Category_tableView.getSelectionModel().getSelectedItem();
    if (selectedCategoryViewModel != null) {
     /* try {
        int categoryId = selectedCategoryViewModel.getIdCategory();
        List<Integer> associatedGoals = goalRepository.getGoalsByCategoryId(categoryId);
        if (associatedGoals.isEmpty()) {
          categoryRepository.deleteCategory(categoryId);
          loadCategories();
        } else {
          errorMessage.setText("Ця категорія має пов'язані елементи і не може бути видалена.");
        }
      } catch (EntityNotFoundException e) {
        e.printStackTrace();
      }*/
    }
  }

  private void onEditClicked() {
    CategoryViewModel selectedCategoryViewModel = Category_tableView.getSelectionModel().getSelectedItem();
    if (selectedCategoryViewModel != null) {
      String newName = addCategory.getText().trim();
      List<Category> existingCategories = categoryRepository.getAllCategories();
      String validationMessage = CategoryValidator.validateCategoryName(newName, existingCategories);
      if (validationMessage == null) {
        try {
          Category updatedCategory = new Category(selectedCategoryViewModel.getIdCategory(), newName);
          categoryRepository.updateCategory(updatedCategory);
          loadCategories();
          addCategory.clear();
        } catch (EntityNotFoundException e) {
          e.printStackTrace();
        }
      } else {
        errorMessage.setText(validationMessage);
      }
    }
  }
}
