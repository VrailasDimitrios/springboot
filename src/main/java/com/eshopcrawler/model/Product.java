package com.eshopcrawler.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "Products")
public class Product {
    @Id
    private String id;
    private String url;
    private String name;
    private String reviewScore;
    private List<String> reviews;
    private SentimentValue sentimentScoreLabel;
    private double sentimentScoreValue;
}
