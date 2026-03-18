package com.coffeeshop.pos.model;

public class Order {

    private int id;
    private String orderType;
    private double totalAmount;
    private String paymentMethod;
    private double amountPaid;
    private double changeAmount;
    private String createdAt;
    private int cashierId;

    public Order() {
    }

    public Order(String orderType, double totalAmount, String paymentMethod,
                 double amountPaid, double changeAmount, String createdAt, int cashierId) {
        this.orderType = orderType;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
        this.changeAmount = changeAmount;
        this.createdAt = createdAt;
        this.cashierId = cashierId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(double changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getCashierId() {
        return cashierId;
    }

    public void setCashierId(int cashierId) {
        this.cashierId = cashierId;
    }
}