package com.eshopcrawler.service;

import com.eshopcrawler.model.Product;
import com.eshopcrawler.model.SentimentValue;
import com.eshopcrawler.repository.ProductRepository;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

import static com.eshopcrawler.model.SentimentValue.*;
import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class SentimentAnalysisService {
    private final ProductRepository productRepository;

    public void sentiment() {
        log.info("Starting sentiment analysis...");

        StanfordCoreNLP pipeline = setupPipeline();
        try {
            int page = 0;
            int size = 100;
            Sort sort = Sort.by("name").ascending();

            while (true) {
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<Product> productsPage = productRepository.findAll(pageable);

                if (productsPage.isEmpty()) {
                    break; // exit loop if there are no more products
                }

                for (Product product : productsPage) {
                    processDocument(pipeline, product);
                }

                page++; // go to the next page
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private StanfordCoreNLP setupPipeline() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,sentiment");
        return new StanfordCoreNLP(props);
    }

    private void processDocument(StanfordCoreNLP pipeline, Product product) {
        try {
            if (isNull(product.getReviews()) || product.getReviews().isEmpty()) {
                log.error("No reviews found in the document: {}", product.getName());
                return;
            }

            double totalSentimentScore = 0;
            int reviewCount = product.getReviews().size();
            for (String review : product.getReviews()) {
                if (!isNull(review) && !review.isEmpty()) {
                    totalSentimentScore += analyzeSentimentScore(pipeline, review);
                }
            }
            double averageSentimentScore = totalSentimentScore / reviewCount;
            String averageSentiment = getSentimentLabel(averageSentimentScore);
            log.info("Product: {} ", product.getName());
            log.info("Average Sentiment Score: {} ", averageSentiment);
            product.setSentimentScoreLabel(SentimentValue.valueOf(averageSentiment));
            product.setSentimentScoreValue(averageSentimentScore);
            productRepository.save(product);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    private static double analyzeSentimentScore(StanfordCoreNLP pipeline, String review) {
        Annotation annotation = new Annotation(review);
        pipeline.annotate(annotation);
        CoreDocument coreDocument = new CoreDocument(annotation);
        List<CoreMap> coreMaps = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        List<CoreSentence> sentences = coreMaps.stream().map(coreMap -> new CoreSentence(coreDocument, coreMap)).toList();
        int totalSentiment = 0;

        for (CoreSentence sentence : sentences) {
            SentimentValue sentimentValue = SentimentValue.fromString(sentence.sentiment());
            if (sentimentValue != null) {
                totalSentiment += sentimentValue.getValue();
            } else {
                log.error("Unexpected sentiment");
            }
        }
        return (double) totalSentiment / sentences.size();
    }

    private String getSentimentLabel(double averageSentimentScore) {
        if (averageSentimentScore > 1) {
            return VERY_POSITIVE.name();
        } else if (averageSentimentScore > 0) {
            return POSITIVE.name();
        } else if (averageSentimentScore == 0) {
            return NEUTRAL.name();
        } else if (averageSentimentScore > -1) {
            return NEGATIVE.name();
        } else {
            return VERY_NEGATIVE.name();
        }
    }


}