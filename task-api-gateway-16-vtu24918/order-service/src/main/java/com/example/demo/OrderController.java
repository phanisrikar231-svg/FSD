package com.example.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final ProductClient productClient;

    public OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @GetMapping
    public String getOrders() {
        String products = productClient.getProducts();
        return "Orders + Products: " + products;
    }
}