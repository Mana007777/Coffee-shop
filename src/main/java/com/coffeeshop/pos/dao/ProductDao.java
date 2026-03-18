package com.coffeeshop.pos.dao;

import com.coffeeshop.pos.config.DatabaseConnection;
import com.coffeeshop.pos.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT id, name, category_id, price, stock_qty, is_active
                FROM products
                ORDER BY name
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Product product = mapResultSetToProduct(resultSet);
                products.add(product);
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch products: " + e.getMessage());
        }

        return products;
    }

    private Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setCategoryId(resultSet.getInt("category_id"));
        product.setPrice(resultSet.getDouble("price"));
        product.setStockQty(resultSet.getInt("stock_qty"));
        product.setActive(resultSet.getInt("is_active") == 1);
        return product;
    }
}