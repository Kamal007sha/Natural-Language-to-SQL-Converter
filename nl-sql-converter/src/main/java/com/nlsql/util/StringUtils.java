package com.nlsql.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtils {
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static String normalize(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.toLowerCase().trim();
    }
    
    public static List<String> tokenize(String text) {
        if (isEmpty(text)) {
            return Arrays.asList();
        }
        
        return Arrays.stream(text.split("\\s+"))
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    public static String removeSpecialCharacters(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
    }
    
    public static double calculateSimilarity(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }
        
        str1 = normalize(str1);
        str2 = normalize(str2);
        
        if (str1.equals(str2)) {
            return 1.0;
        }
        
        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) {
            return 1.0;
        }
        
        int distance = levenshteinDistance(str1, str2);
        return (double) (maxLength - distance) / maxLength;
    }
    
    private static int levenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];
        
        for (int i = 0; i <= str1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= str2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[str1.length()][str2.length()];
    }
}