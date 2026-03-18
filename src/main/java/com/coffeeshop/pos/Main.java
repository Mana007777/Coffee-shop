package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.model.Product;
import com.coffeeshop.pos.service.ProductService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Coffee POS...");

        DatabaseInitializer.initializeDatabase();
        DataSeeder.seedData();

        ProductService productService = new ProductService();
        List<Product> products = productService.getAvailableProducts();

        System.out.println("Available products:");
        for (Product product : products) {
            System.out.println(
                    product.getId() + " | " +
                            product.getName() + " | " +
                            product.getPrice() + " | stock: " +
                            product.getStockQty()
            );
        }

        System.out.println("Database is ready.");
    }
}