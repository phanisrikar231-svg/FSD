package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateAndGetProduct() {

        // Create product
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(100);

        ResponseEntity<Product> postResponse =
                restTemplate.postForEntity("/products", product, Product.class);

        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());

        // Get products
        ResponseEntity<Product[]> getResponse =
                restTemplate.getForEntity("/products", Product[].class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertTrue(getResponse.getBody().length > 0);
    }
}