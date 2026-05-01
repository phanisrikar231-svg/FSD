package com.example.demo;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private Map<Long, Product> productDB = new HashMap<>();
    private Long idCounter = 1L;

    @GetMapping
    public Collection<Product> getAllProducts() {
        return productDB.values();
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        if (!productDB.containsKey(id)) {
            throw new RuntimeException("Product not found");
        }
        return productDB.get(id);
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody Product product) {
        product.setId(idCounter++);
        productDB.put(product.getId(), product);
        return product;
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id,
                                 @Valid @RequestBody Product product) {
        if (!productDB.containsKey(id)) {
            throw new RuntimeException("Product not found");
        }
        product.setId(id);
        productDB.put(id, product);
        return product;
    }
    
 // main branch change

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        if (!productDB.containsKey(id)) {
            throw new RuntimeException("Product not found");
        }
        productDB.remove(id);
        return "Deleted";
        
     // feature branch change
    }
}