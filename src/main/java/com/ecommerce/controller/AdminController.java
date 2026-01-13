package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ==================== PRODUCTS ====================
    
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        try {
            product.setActive(true);
            Product savedProduct = productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error adding product: " + e.getMessage()));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    if (productDetails.getName() != null) product.setName(productDetails.getName());
                    if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription());
                    if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
                    if (productDetails.getStockQuantity() != null) product.setStockQuantity(productDetails.getStockQuantity());
                    if (productDetails.getDiscount() != null) product.setDiscount(productDetails.getDiscount());
                    if (productDetails.getCategory() != null) product.setCategory(productDetails.getCategory());
                    
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== ORDERS ====================
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        
        if (statusStr == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Status is required"));
        }

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());
            
            return orderRepository.findById(id)
                    .map(order -> {
                        order.setStatus(status);
                        Order updatedOrder = orderRepository.save(order);
                        return ResponseEntity.ok(updatedOrder);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status: " + statusStr));
        }
    }

    // ==================== DASHBOARD STATS ====================
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProducts", productRepository.count());
            stats.put("totalOrders", orderRepository.count());
            stats.put("totalRevenue", orderRepository.findAll().stream()
                    .mapToDouble(order -> order.getTotalAmount().doubleValue())
                    .sum());
            stats.put("totalPendingOrders", orderRepository.findByStatus(Order.OrderStatus.PENDING).size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error fetching stats: " + e.getMessage()));
        }
    }
}
