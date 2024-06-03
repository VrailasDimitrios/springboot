// src/main/java/com/eshopcrawler/repository/ProductRepository.java
package com.eshopcrawler.repository;

import com.eshopcrawler.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Page<Product> findAll(Pageable pageable);
}
