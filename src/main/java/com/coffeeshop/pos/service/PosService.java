package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.OrderDao;
import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.config.DatabaseConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.coffeeshop.pos.dao.ProductDao;
import java.util.ArrayList;
import java.util.List;

public class PosService {

    private final List<CartItem> cartItems = new ArrayList<>();
    private final OrderDao orderDao;

    public PosService() {
        this.orderDao = new OrderDao();
    }

    public void addToCart(Product product, int quantity) {
        CartItem cartItem = new CartItem(product, quantity);
        cartItems.add(cartItem);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double calculateTotal() {
        double total = 0.0;

        for (CartItem item : cartItems) {
            total += item.getSubtotal();
        }

        return total;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public int checkout(String orderType, String paymentMethod, double amountPaid, int cashierId) {

        double totalAmount = calculateTotal();
        double changeAmount = amountPaid - totalAmount;

        Order order = new Order(
                orderType,
                totalAmount,
                paymentMethod,
                amountPaid,
                changeAmount,
                java.time.LocalDateTime.now().toString(),
                cashierId
        );

        try (Connection connection = DatabaseConnection.connect()) {

            // START TRANSACTION
            connection.setAutoCommit(false);

            int orderId = orderDao.insertOrder(connection, order);

            if (orderId == -1) {
                connection.rollback();
                return -1;
            }

            // Insert order items
            orderDao.insertOrderItems(connection, orderId, cartItems);

            // Reduce stock
            for (CartItem item : cartItems) {
                new ProductDao().reduceStock(
                        connection,
                        item.getProduct().getId(),
                        item.getQuantity()
                );
            }

            // COMMIT
            connection.commit();

            return orderId;

        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
            return -1;
        }
    }
}