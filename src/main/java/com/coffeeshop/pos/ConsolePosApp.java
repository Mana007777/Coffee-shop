package com.coffeeshop.pos;

import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.model.User;
import com.coffeeshop.pos.service.PosService;
import com.coffeeshop.pos.service.ProductService;
import com.coffeeshop.pos.service.UserService;
import com.coffeeshop.pos.service.OrderService;
import java.util.List;
import java.util.Scanner;

public class ConsolePosApp {

    private final UserService userService;
    private final ProductService productService;
    private final PosService posService;
    private final Scanner scanner;
    private final OrderService orderService;

    public ConsolePosApp() {
        this.userService = new UserService();
        this.productService = new ProductService();
        this.posService = new PosService();
        this.scanner = new Scanner(System.in);
        this.orderService = new OrderService();
    }

    public void run() {
        User user = login();

        if (user == null) {
            System.out.println("Login failed.");
            return;
        }

        System.out.println("Welcome, " + user.getUsername() + " [" + user.getRole() + "]");

        boolean running = true;

        while (running) {
            printAvailableProducts();
            printMenu();

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addProductToCart();
                case 2 -> updateCartItem();
                case 3 -> removeCartItem();
                case 4 -> posService.printCart();
                case 5 -> {
                    boolean success = checkout(user);
                    if (success) {
                        running = false;
                    }
                }
                case 6 -> orderService.printSalesHistory();
                case 7 -> viewOrderDetails();
                case 8 -> {
                    posService.clearCart();
                    System.out.println("Order cancelled.");
                    running = false;
                }
                default -> System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }

    private User login() {
        return userService.login("admin", "admin123");
    }

    private void printAvailableProducts() {
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
    }

    private void printMenu() {
        System.out.println("""
            
            Choose an option:
            1. Add product to cart
            2. Update cart item quantity
            3. Remove item from cart
            4. View cart
            5. Checkout
            6. View sales history
            7. View order details
            8. Cancel order
            """);
    }

    private void addProductToCart() {
        System.out.print("Enter product id: ");
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

        boolean added = posService.addToCart(selectedProduct, quantity);

        if (!added) {
            int alreadyInCart = posService.getQuantityInCart(selectedProduct.getId());

            System.out.println(
                    "Cannot add product. Stock available: " +
                            selectedProduct.getStockQty() +
                            ", already in cart: " +
                            alreadyInCart
            );
            return;
        }

        System.out.println("Product added to cart.");
        posService.printCart();
    }

    private void updateCartItem() {
        if (posService.isCartEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        posService.printCart();

        System.out.print("Enter product id to update: ");
        int productId = scanner.nextInt();

        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();

        boolean updated = posService.updateCartItemQuantity(productId, newQuantity);

        if (updated) {
            System.out.println("Cart updated.");
        } else {
            System.out.println("Failed to update cart item.");
        }

        posService.printCart();
    }

    private void removeCartItem() {
        if (posService.isCartEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        posService.printCart();

        System.out.print("Enter product id to remove: ");
        int productId = scanner.nextInt();

        boolean removed = posService.removeFromCart(productId);

        if (removed) {
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("Item not found in cart.");
        }

        posService.printCart();
    }

    private boolean checkout(User user) {
        if (posService.isCartEmpty()) {
            System.out.println("Cart is empty. Cannot checkout.");
            return false;
        }

        double total = posService.calculateTotal();
        System.out.println("\nFinal total = $" + total);

        System.out.print("Enter amount paid: ");
        double amountPaid = scanner.nextDouble();

        if (amountPaid < total) {
            System.out.println("Insufficient payment.");
            return false;
        }

        int orderId = posService.checkout("TAKEAWAY", "CASH", amountPaid, user.getId());

        if (orderId != -1) {
            System.out.println("Order saved successfully. Order ID: " + orderId);
            System.out.println("Change = $" + (amountPaid - total));
            posService.clearCart();
            return true;
        }

        System.out.println("Failed to save order.");
        return false;
    }
    private void viewOrderDetails() {
        System.out.print("Enter order id: ");
        int orderId = scanner.nextInt();
        orderService.printOrderDetails(orderId);
    }
}