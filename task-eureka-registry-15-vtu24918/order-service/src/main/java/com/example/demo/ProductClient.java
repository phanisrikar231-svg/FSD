package com.example.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    public String getProducts() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/products";
        return restTemplate.getForObject(url, String.class);
    }
}