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
    public Product findById(int id) {
        String sql = """
            SELECT id, name, category_id, price, stock_qty, is_active
            FROM products
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToProduct(resultSet);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch product by id: " + e.getMessage());
        }

        return null;
    }
    public void reduceStock(Connection connection, int productId, int quantity) throws SQLException {
        String sql = """
            UPDATE products
            SET stock_qty = stock_qty - ?
            WHERE id = ?
            """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, productId);
            statement.executeUpdate();
        }
    }
    public boolean insertProduct(Product product) {
        String sql = """
            INSERT INTO products (name, category_id, price, stock_qty, is_active)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, product.getName());
            statement.setInt(2, product.getCategoryId());
            statement.setDouble(3, product.getPrice());
            statement.setInt(4, product.getStockQty());
            statement.setInt(5, product.isActive() ? 1 : 0);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to insert product: " + e.getMessage());
            return false;
        }
    }
    public boolean updateProductPrice(int productId, double newPrice) {
        String sql = """
            UPDATE products
            SET price = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, newPrice);
            statement.setInt(2, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to update product price: " + e.getMessage());
            return false;
        }
    }
    public boolean updateProductStock(int productId, int newStockQty) {
        String sql = """
            UPDATE products
            SET stock_qty = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, newStockQty);
            statement.setInt(2, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to update product stock: " + e.getMessage());
            return false;
        }
    }
    public boolean updateProductActiveStatus(int productId, boolean active) {
        String sql = """
            UPDATE products
            SET is_active = ?
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, active ? 1 : 0);
            statement.setInt(2, productId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Failed to update product active status: " + e.getMessage());
            return false;
        }
    }
}