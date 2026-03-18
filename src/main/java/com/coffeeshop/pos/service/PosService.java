package com.coffeeshop.pos.service;

import com.coffeeshop.pos.config.DatabaseConnection;
import com.coffeeshop.pos.dao.OrderDao;
import com.coffeeshop.pos.dao.ProductDao;
import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PosService {

    private final List<CartItem> cartItems = new ArrayList<>();
    private final OrderDao orderDao;
    private final ProductDao productDao;

    public PosService() {
        this.orderDao = new OrderDao();
        this.productDao = new ProductDao();
    }

    public boolean addToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        int existingQuantity = getQuantityInCart(product.getId());
        int newTotalQuantity = existingQuantity + quantity;

        if (newTotalQuantity > product.getStockQty()) {
            return false;
        }

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(newTotalQuantity);
                return true;
            }
        }

        CartItem cartItem = new CartItem(product, quantity);
        cartItems.add(cartItem);
        return true;
    }

    public int getQuantityInCart(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                return item.getQuantity();
            }
        }
        return 0;
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

    public void clearCart() {
        cartItems.clear();
    }

    public void printCart() {
        if (cartItems.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\nCurrent cart:");
        for (CartItem item : cartItems) {
            System.out.println(
                    item.getProduct().getId() + " | " +
                            item.getProduct().getName() + " x " +
                            item.getQuantity() + " = $" +
                            item.getSubtotal()
            );
        }

        System.out.println("Total = $" + calculateTotal());
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
                LocalDateTime.now().toString(),
                cashierId
        );

        try (Connection connection = DatabaseConnection.connect()) {
            connection.setAutoCommit(false);

            int orderId = orderDao.insertOrder(connection, order);

            if (orderId == -1) {
                connection.rollback();
                return -1;
            }

            orderDao.insertOrderItems(connection, orderId, cartItems);

            for (CartItem item : cartItems) {
                productDao.reduceStock(
                        connection,
                        item.getProduct().getId(),
                        item.getQuantity()
                );
            }

            connection.commit();
            return orderId;

        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
            return -1;
        }
    }
}