package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
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
        Scanner scanner = new Scanner(System.in);

        boolean addingProducts = true;

        while (addingProducts) {
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

            System.out.print("\nEnter product id: ");
            int productId = scanner.nextInt();

            Product selectedProduct = productService.getAvailableProductById(productId);

            if (selectedProduct == null) {
                System.out.println("Invalid product.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();

            if (quantity <= 0) {
                System.out.println("Invalid quantity.");
                continue;
            }

            if (quantity > selectedProduct.getStockQty()) {
                System.out.println("Not enough stock.");
                continue;
            }

            posService.addToCart(selectedProduct, quantity);
            posService.printCart();

            System.out.print("\nAdd another product? (y/n): ");
            String answer = scanner.next();

            if (!answer.equalsIgnoreCase("y")) {
                addingProducts = false;
            }
        }

        if (posService.isCartEmpty()) {
            System.out.println("Cart is empty. Checkout cancelled.");
            return;
        }

        double total = posService.calculateTotal();
        System.out.println("\nFinal total = $" + total);

        System.out.print("Enter amount paid: ");
        double amountPaid = scanner.nextDouble();

        if (amountPaid < total) {
            System.out.println("Insufficient payment.");
            return;
        }

        int orderId = posService.checkout("TAKEAWAY", "CASH", amountPaid, user.getId());

        if (orderId != -1) {
            System.out.println("Order saved successfully. Order ID: " + orderId);
            System.out.println("Change = $" + (amountPaid - total));
            posService.clearCart();
        } else {
            System.out.println("Failed to save order.");
        }
    }
}