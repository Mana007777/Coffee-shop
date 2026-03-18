package com.coffeeshop.pos.dao;

import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.coffeeshop.pos.config.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import com.coffeeshop.pos.model.OrderItem;
import com.coffeeshop.pos.model.ProductSalesReport;

public class OrderDao {

    public int insertOrder(Connection connection, Order order) throws SQLException {
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

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

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
        }

        return -1;
    }

    public void insertOrderItems(Connection connection, int orderId, List<CartItem> cartItems) throws SQLException {
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

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

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
        }
    }
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();

        String sql = """
            SELECT id, order_type, total_amount, payment_method,
                   amount_paid, change_amount, created_at, cashier_id
            FROM orders
            ORDER BY id DESC
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Order order = mapResultSetToOrder(resultSet);
                orders.add(order);
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch orders: " + e.getMessage());
        }

        return orders;
    }
    private Order mapResultSetToOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setOrderType(resultSet.getString("order_type"));
        order.setTotalAmount(resultSet.getDouble("total_amount"));
        order.setPaymentMethod(resultSet.getString("payment_method"));
        order.setAmountPaid(resultSet.getDouble("amount_paid"));
        order.setChangeAmount(resultSet.getDouble("change_amount"));
        order.setCreatedAt(resultSet.getString("created_at"));
        order.setCashierId(resultSet.getInt("cashier_id"));
        return order;
    }
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();

        String sql = """
            SELECT oi.id,
                   oi.order_id,
                   oi.product_id,
                   p.name AS product_name,
                   oi.quantity,
                   oi.unit_price,
                   oi.subtotal,
                   oi.note
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            WHERE oi.order_id = ?
            ORDER BY oi.id
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItem item = mapResultSetToOrderItem(resultSet);
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch order items: " + e.getMessage());
        }

        return items;
    }
    private OrderItem mapResultSetToOrderItem(ResultSet resultSet) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(resultSet.getInt("id"));
        item.setOrderId(resultSet.getInt("order_id"));
        item.setProductId(resultSet.getInt("product_id"));
        item.setProductName(resultSet.getString("product_name"));
        item.setQuantity(resultSet.getInt("quantity"));
        item.setUnitPrice(resultSet.getDouble("unit_price"));
        item.setSubtotal(resultSet.getDouble("subtotal"));
        item.setNote(resultSet.getString("note"));
        return item;
    }
    public List<Order> getOrdersByDate(String date) {
        List<Order> orders = new ArrayList<>();

        String sql = """
            SELECT id, order_type, total_amount, payment_method,
                   amount_paid, change_amount, created_at, cashier_id
            FROM orders
            WHERE DATE(created_at) = ?
            ORDER BY id DESC
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Order order = mapResultSetToOrder(resultSet);
                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch orders by date: " + e.getMessage());
        }

        return orders;
    }
    public double getTotalSalesByDate(String date) {
        String sql = """
            SELECT COALESCE(SUM(total_amount), 0)
            FROM orders
            WHERE DATE(created_at) = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to calculate total sales: " + e.getMessage());
        }

        return 0.0;
    }
    public List<Order> getOrdersBetweenDates(String startDate, String endDate) {
        List<Order> orders = new ArrayList<>();

        String sql = """
            SELECT id, order_type, total_amount, payment_method,
                   amount_paid, change_amount, created_at, cashier_id
            FROM orders
            WHERE DATE(created_at) BETWEEN ? AND ?
            ORDER BY id DESC
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, startDate);
            statement.setString(2, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Order order = mapResultSetToOrder(resultSet);
                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch orders between dates: " + e.getMessage());
        }

        return orders;
    }
    public double getTotalSalesBetweenDates(String startDate, String endDate) {
        String sql = """
            SELECT COALESCE(SUM(total_amount), 0)
            FROM orders
            WHERE DATE(created_at) BETWEEN ? AND ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, startDate);
            statement.setString(2, endDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to calculate total sales between dates: " + e.getMessage());
        }

        return 0.0;
    }
    public List<ProductSalesReport> getTopSellingProducts() {
        List<ProductSalesReport> reports = new ArrayList<>();

        String sql = """
            SELECT p.name AS product_name,
                   COALESCE(SUM(oi.quantity), 0) AS total_quantity_sold,
                   COALESCE(SUM(oi.subtotal), 0) AS total_revenue
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            GROUP BY oi.product_id, p.name
            ORDER BY total_quantity_sold DESC, total_revenue DESC
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                ProductSalesReport report = mapResultSetToProductSalesReport(resultSet);
                reports.add(report);
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch top-selling products: " + e.getMessage());
        }

        return reports;
    }
    private ProductSalesReport mapResultSetToProductSalesReport(ResultSet resultSet) throws SQLException {
        ProductSalesReport report = new ProductSalesReport();
        report.setProductName(resultSet.getString("product_name"));
        report.setTotalQuantitySold(resultSet.getInt("total_quantity_sold"));
        report.setTotalRevenue(resultSet.getDouble("total_revenue"));
        return report;
    }
}