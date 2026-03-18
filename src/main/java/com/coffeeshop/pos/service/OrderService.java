package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.OrderDao;
import com.coffeeshop.pos.model.Order;

import java.util.List;

public class OrderService {

    private final OrderDao orderDao;

    public OrderService() {
        this.orderDao = new OrderDao();
    }

    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public void printSalesHistory() {
        List<Order> orders = getAllOrders();

        if (orders.isEmpty()) {
            System.out.println("No sales found.");
            return;
        }

        System.out.println("\nSales History:");
        for (Order order : orders) {
            System.out.println(
                    "Order ID: " + order.getId() +
                            " | Type: " + order.getOrderType() +
                            " | Total: $" + order.getTotalAmount() +
                            " | Paid: $" + order.getAmountPaid() +
                            " | Change: $" + order.getChangeAmount() +
                            " | Payment: " + order.getPaymentMethod() +
                            " | Cashier ID: " + order.getCashierId() +
                            " | Date: " + order.getCreatedAt()
            );
        }
    }
}