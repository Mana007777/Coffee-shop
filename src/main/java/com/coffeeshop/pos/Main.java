package com.coffeeshop.pos;

import com.coffeeshop.pos.config.DataSeeder;
import com.coffeeshop.pos.config.DatabaseInitializer;
import com.coffeeshop.pos.dao.ProductDao;
import com.coffeeshop.pos.model.Product;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Coffee POS...");

        DatabaseInitializer.initializeDatabase();
        DataSeeder.seedData();

        ProductDao productDao = new ProductDao();
        List<Product> products = productDao.getAllProducts();

        System.out.println("Products in database:");
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