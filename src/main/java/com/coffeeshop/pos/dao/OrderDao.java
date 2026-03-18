package com.coffeeshop.pos.dao;

import com.coffeeshop.pos.config.DatabaseConnection;
import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDao {

    public int insertOrder(Order order) {
        String sql = """
                INSERT INTO orders (
                    order_type,
                    total_amount,
                    payment_method,
                    amount_paid,
                    change_amount,
                    created_at,
                    cashier_id
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, order.getOrderType());
            statement.setDouble(2, order.getTotalAmount());
            statement.setString(3, order.getPaymentMethod());
            statement.setDouble(4, order.getAmountPaid());
            statement.setDouble(5, order.getChangeAmount());
            statement.setString(6, order.getCreatedAt());
            statement.setInt(7, order.getCashierId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to insert order: " + e.getMessage());
        }

        return -1;
    }

    public void insertOrderItems(int orderId, List<CartItem> cartItems) {
        String sql = """
                INSERT INTO order_items (
                    order_id,
                    product_id,
                    quantity,
                    unit_price,
                    subtotal,
                    note
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (CartItem cartItem : cartItems) {
                statement.setInt(1, orderId);
                statement.setInt(2, cartItem.getProduct().getId());
                statement.setInt(3, cartItem.getQuantity());
                statement.setDouble(4, cartItem.getProduct().getPrice());
                statement.setDouble(5, cartItem.getSubtotal());
                statement.setString(6, null);
                statement.addBatch();
            }

            statement.executeBatch();

        } catch (SQLException e) {
            System.out.println("Failed to insert order items: " + e.getMessage());
        }
    }
}