package com.coffeeshop.pos.model;

public class CartItem {

    private Product product;
    private int quantity;
    private double subtotal;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        recalculateSubtotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    private void recalculateSubtotal() {
        if (product != null) {
            this.subtotal = product.getPrice() * quantity;
        }
    }
    @Override
    public String toString() {
        return product.getName() + " x " + quantity + " = $" + subtotal;
    }
}