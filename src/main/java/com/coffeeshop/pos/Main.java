package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.model.CartItem;
import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.PosService;
import com.coffeeshop.pos.service.ProductService;
import com.coffeeshop.pos.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Coffee POS...");

        DatabaseInitializer.initializeDatabase();
        DataSeeder.seedData();

        UserService userService = new UserService();
        User user = userService.login("admin", "admin123");

        if (user == null) {
            System.out.println("Login failed.");
            return;
        }

        System.out.println("Welcome, " + user.getUsername() + " [" + user.getRole() + "]");

        ProductService productService = new ProductService();
        PosService posService = new PosService();

        List<Product> products = productService.getAvailableProducts();

        System.out.println("\nAvailable products:");
        for (Product product : products) {
            System.out.println(
                    product.getId() + " | " +
                            product.getName() + " | $" +
                            product.getPrice() + " | stock: " +
                            product.getStockQty()
            );
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("\nEnter product id: ");
        int productId = scanner.nextInt();

        Product selectedProduct = productService.getAvailableProductById(productId);

        if (selectedProduct == null) {
            System.out.println("Invalid product.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        if (quantity <= 0) {
            System.out.println("Invalid quantity.");
            return;
        }

        if (quantity > selectedProduct.getStockQty()) {
            System.out.println("Not enough stock.");
            return;
        }

        posService.addToCart(selectedProduct, quantity);

        System.out.println("\nCart:");
        for (CartItem item : posService.getCartItems()) {
            System.out.println(
                    item.getProduct().getName() + " x " +
                            item.getQuantity() + " = $" +
                            item.getSubtotal()
            );
        }

        System.out.println("Total = $" + posService.calculateTotal());
    }
}