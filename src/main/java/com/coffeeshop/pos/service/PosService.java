package com.coffeeshop.pos.service;

import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PosService {

    private final List<CartItem> cartItems = new ArrayList<>();

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
}