package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.OrderDao;
import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.Product;

import java.time.LocalDateTime;
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
                LocalDateTime.now().toString(),
                cashierId
        );

        int orderId = orderDao.insertOrder(order);

        if (orderId != -1) {
            orderDao.insertOrderItems(orderId, cartItems);
        }

        return orderId;
    }
}