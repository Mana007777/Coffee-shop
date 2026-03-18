package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.OrderDao;
import com.coffeeshop.pos.model.Order;
import com.coffeeshop.pos.model.OrderItem;
import com.coffeeshop.pos.model.ProductSalesReport;
import java.util.List;

public class OrderService {

    private final OrderDao orderDao;

    public OrderService() {
        this.orderDao = new OrderDao();
    }

    public List<Order> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderDao.getOrderItemsByOrderId(orderId);
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

    public void printOrderDetails(int orderId) {
        List<OrderItem> items = getOrderItemsByOrderId(orderId);

        if (items.isEmpty()) {
            System.out.println("No items found for order ID: " + orderId);
            return;
        }

        System.out.println("\nOrder Details for Order ID: " + orderId);
        for (OrderItem item : items) {
            System.out.println(
                    item.getProductName() +
                            " | Qty: " + item.getQuantity() +
                            " | Unit Price: $" + item.getUnitPrice() +
                            " | Subtotal: $" + item.getSubtotal()
            );
        }
    }
    public List<Order> getOrdersByDate(String date) {
        return orderDao.getOrdersByDate(date);
    }

    public double getTotalSalesByDate(String date) {
        return orderDao.getTotalSalesByDate(date);
    }

    public void printDailySalesReport(String date) {
        List<Order> orders = getOrdersByDate(date);
        double totalSales = getTotalSalesByDate(date);

        System.out.println("\nDaily Sales Report for " + date);
        System.out.println("Number of orders: " + orders.size());
        System.out.println("Total sales: $" + totalSales);
    }
    public List<Order> getOrdersBetweenDates(String startDate, String endDate) {
        return orderDao.getOrdersBetweenDates(startDate, endDate);
    }

    public double getTotalSalesBetweenDates(String startDate, String endDate) {
        return orderDao.getTotalSalesBetweenDates(startDate, endDate);
    }

    public void printSalesReportBetweenDates(String startDate, String endDate) {
        List<Order> orders = getOrdersBetweenDates(startDate, endDate);
        double totalSales = getTotalSalesBetweenDates(startDate, endDate);

        System.out.println("\nSales Report from " + startDate + " to " + endDate);
        System.out.println("Number of orders: " + orders.size());
        System.out.println("Total sales: $" + totalSales);
    }
    public List<ProductSalesReport> getTopSellingProducts() {
        return orderDao.getTopSellingProducts();
    }

    public void printTopSellingProductsReport() {
        List<ProductSalesReport> reports = getTopSellingProducts();

        if (reports.isEmpty()) {
            System.out.println("No product sales data found.");
            return;
        }

        System.out.println("\nTop-Selling Products Report:");
        for (ProductSalesReport report : reports) {
            System.out.println(
                    report.getProductName() +
                            " | Quantity Sold: " + report.getTotalQuantitySold() +
                            " | Revenue: $" + report.getTotalRevenue()
            );
        }

    }
    public String buildTodaySalesReport() {
        String today = java.time.LocalDate.now().toString();
        return buildSalesReportByDate(today);
    }

    public String buildSalesReportByDate(String date) {
        List<Order> orders = getOrdersByDate(date);
        double totalSales = getTotalSalesByDate(date);

        return "Sales Report for " + date + "\n" +
                "Number of orders: " + orders.size() + "\n" +
                "Total sales: $" + totalSales;
    }

    public String buildSalesReportBetweenDates(String startDate, String endDate) {
        List<Order> orders = getOrdersBetweenDates(startDate, endDate);
        double totalSales = getTotalSalesBetweenDates(startDate, endDate);

        return "Sales Report from " + startDate + " to " + endDate + "\n" +
                "Number of orders: " + orders.size() + "\n" +
                "Total sales: $" + totalSales;
    }

    public String buildTopSellingProductsReport() {
        List<ProductSalesReport> reports = getTopSellingProducts();

        if (reports.isEmpty()) {
            return "No product sales data found.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Top-Selling Products Report:\n");

        for (ProductSalesReport report : reports) {
            builder.append(report.getProductName())
                    .append(" | Quantity Sold: ")
                    .append(report.getTotalQuantitySold())
                    .append(" | Revenue: $")
                    .append(report.getTotalRevenue())
                    .append("\n");
        }

        return builder.toString();
    }
}