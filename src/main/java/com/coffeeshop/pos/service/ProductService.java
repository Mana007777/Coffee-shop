package com.coffeeshop.pos.service;

import com.coffeeshop.pos.dao.ProductDao;
import com.coffeeshop.pos.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductDao productDao;

    public ProductService() {
        this.productDao = new ProductDao();
    }

    public List<Product> getAllProducts() {
        return productDao.getAllProducts();
    }

    public List<Product> getActiveProducts() {
        return productDao.getAllProducts()
                .stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    public List<Product> getAvailableProducts() {
        return productDao.getAllProducts()
                .stream()
                .filter(Product::isActive)
                .filter(product -> product.getStockQty() > 0)
                .collect(Collectors.toList());
    }
}