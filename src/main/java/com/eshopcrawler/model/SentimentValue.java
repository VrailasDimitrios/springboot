package com.eshopcrawler.model;

public enum SentimentValue {
    VERY_POSITIVE(2),
    POSITIVE(1),
    NEUTRAL(0),
    NEGATIVE(-1),
    VERY_NEGATIVE(-2);

    private final int value;

    SentimentValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SentimentValue fromString(String sentiment) {
        try {
            return SentimentValue.valueOf(sentiment.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}