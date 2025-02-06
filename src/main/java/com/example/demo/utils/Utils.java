package com.example.demo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demo.utils.Constants.DATE_TIME_PATTERN;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.UUID.randomUUID;

public class Utils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern(DATE_TIME_PATTERN);

    public static String formatDate(LocalDateTime dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static String generateFileToken() {
        return randomUUID().toString();
    }
}
