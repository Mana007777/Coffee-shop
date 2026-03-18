package com.coffeeshop.pos.model;

public class Product {

    private int id;
    private String name;
    private int categoryId;
    private double price;
    private int stockQty;
    private boolean active;

    public Product() {
    }

    public Product(int id, String name, int categoryId, double price, int stockQty, boolean active) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQty = stockQty;
        this.active = active;
    }

    public Product(String name, int categoryId, double price, int stockQty, boolean active) {
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.stockQty = stockQty;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}