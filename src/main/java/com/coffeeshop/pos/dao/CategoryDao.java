package com.coffeeshop.pos.dao;

import com.coffeeshop.pos.config.DatabaseConnection;
import com.coffeeshop.pos.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        String sql = """
                SELECT id, name
                FROM categories
                ORDER BY name
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categories.add(mapResultSetToCategory(resultSet));
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch categories: " + e.getMessage());
        }

        return categories;
    }

    public boolean insertCategory(Category category) {
        String sql = """
                INSERT INTO categories (name)
                VALUES (?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category.getName());
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to insert category: " + e.getMessage());
            return false;
        }
    }

    public Category findById(int id) {
        String sql = """
                SELECT id, name
                FROM categories
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToCategory(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch category by id: " + e.getMessage());
        }

        return null;
    }

    private Category mapResultSetToCategory(ResultSet resultSet) throws SQLException {
        Category category = new Category();
        category.setId(resultSet.getInt("id"));
        category.setName(resultSet.getString("name"));
        return category;
    }
}