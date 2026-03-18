package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.CategoryDao;
import com.coffeeshop.pos.model.Category;

import java.util.List;

public class CategoryService {

    private final CategoryDao categoryDao;

    public CategoryService() {
        this.categoryDao = new CategoryDao();
    }

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public boolean addCategory(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        Category category = new Category(name.trim());
        return categoryDao.insertCategory(category);
    }

    public Category getCategoryById(int id) {
        if (id <= 0) {
            return null;
        }

        return categoryDao.findById(id);
    }

    public void printCategories() {
        List<Category> categories = getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        System.out.println("\nCategories:");
        for (Category category : categories) {
            System.out.println(category.getId() + " | " + category.getName());
        }
    }
}