package com.eshopcrawler.controller;

import com.eshopcrawler.model.Product;
import com.eshopcrawler.repository.ProductRepository;
import com.eshopcrawler.service.SentimentAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin // Enable CORS
@RequiredArgsConstructor
public class ProductController {
    private final ProductRepository productRepository;
    private final SentimentAnalysisService sentimentAnalysisService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productRepository.findById(id).orElse(null);
    }

    @GetMapping("/sentiment")
    public void analyzeAllProductsSentiments() {
        sentimentAnalysisService.sentiment();
    }
}
