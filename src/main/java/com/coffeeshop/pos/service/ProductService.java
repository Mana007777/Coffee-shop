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
    public Product getAvailableProductById(int id) {
        Product product = productDao.findById(id);

        if (product == null) {
            return null;
        }

        if (!product.isActive() || product.getStockQty() <= 0) {
            return null;
        }

        return product;
    }
    public boolean addProduct(String name, int categoryId, double price, int stockQty) {
        if (name == null || name.isBlank()) {
            return false;
        }

        if (categoryId <= 0 || price < 0 || stockQty < 0) {
            return false;
        }

        Product product = new Product(name, categoryId, price, stockQty, true);
        return productDao.insertProduct(product);
    }
    public boolean updateProductPrice(int productId, double newPrice) {
        if (productId <= 0 || newPrice < 0) {
            return false;
        }

        return productDao.updateProductPrice(productId, newPrice);
    }
    public boolean updateProductStock(int productId, int newStockQty) {
        if (productId <= 0 || newStockQty < 0) {
            return false;
        }

        return productDao.updateProductStock(productId, newStockQty);
    }
    public boolean activateProduct(int productId) {
        if (productId <= 0) {
            return false;
        }

        return productDao.updateProductActiveStatus(productId, true);
    }
    public boolean deactivateProduct(int productId) {
        if (productId <= 0) {
            return false;
        }

        return productDao.updateProductActiveStatus(productId, false);
    }
}