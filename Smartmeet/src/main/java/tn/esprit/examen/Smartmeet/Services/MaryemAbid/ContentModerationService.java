package tn.esprit.examen.Smartmeet.Services.MaryemAbid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ContentModerationService {

    // Sample sets of prohibited content - in production, these would be more extensive
    private final Set<String> badWords = new HashSet<>(Arrays.asList(
            "fuck", "shit", "ass", "bitch", "bastard", "damn", "cunt", "dick", "pussy", 
            "asshole", "whore", "slut", "douchebag", "motherfucker", "piss", "retard", "nigger",
            "faggot", "gay", "dyke", "idiot", "moron", "stupid"
    ));
    
    private final Set<String> illegalContentPatterns = new HashSet<>(Arrays.asList(
            "(?i)child.*porn", "(?i)terrorism", "(?i)bomb making", "(?i)illegal drugs", 
            "(?i)how to hack", "(?i)pirated", "(?i)stolen", "(?i)fake news"
    ));

    /**
     * Checks if content contains prohibited bad words or offensive language
     * @param content The text content to check
     * @return true if content passes moderation (no bad words), false otherwise
     */
    public boolean checkForBadWords(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true; // Empty content passes moderation
        }
        
        String[] words = content.toLowerCase().split("\\s+");
        for (String word : words) {
            // Remove punctuation
            word = word.replaceAll("[^a-zA-Z]", "");
            if (badWords.contains(word)) {
                log.warn("Content moderation failed: Bad word detected - '{}'", word);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if content contains potentially illegal phrases or patterns
     * @param content The text content to check
     * @return true if content passes moderation (no illegal content), false otherwise
     */
    public boolean checkForIllegalContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true; // Empty content passes moderation
        }
        
        for (String pattern : illegalContentPatterns) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(content);
            if (m.find()) {
                log.warn("Content moderation failed: Illegal content pattern detected - '{}'", pattern);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Checks if content contains brand names in negative contexts (bad publicity)
     * @param content The text content to check
     * @return true if content passes moderation (no bad publicity), false otherwise
     */
    public boolean checkForBadPublicity(String content) {
        if (content == null || content.trim().isEmpty()) {
            return true; // Empty content passes moderation
        }
        
        // Sample set of major brand names to check
        Set<String> brands = new HashSet<>(Arrays.asList(
                "apple", "google", "microsoft", "amazon", "facebook", "twitter", 
                "instagram", "tesla", "nike", "adidas", "coca-cola", "pepsi"
        ));
        
        // Negative contexts to check
        Set<String> negativeContexts = new HashSet<>(Arrays.asList(
                "terrible", "awful", "worst", "bad", "hate", "sucks", "garbage", 
                "fraud", "scam", "cheat", "lie", "steal", "corrupt"
        ));
        
        // Check if any brand appears in close proximity to negative words
        String lowercaseContent = content.toLowerCase();
        for (String brand : brands) {
            if (lowercaseContent.contains(brand)) {
                // Check if any negative word is within 10 words of the brand
                int brandIndex = lowercaseContent.indexOf(brand);
                String[] words = lowercaseContent.split("\\s+");
                
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains(brand)) {
                        // Check 5 words before and after
                        int start = Math.max(0, i - 5);
                        int end = Math.min(words.length, i + 5);
                        
                        for (int j = start; j < end; j++) {
                            if (negativeContexts.contains(words[j].replaceAll("[^a-zA-Z]", ""))) {
                                log.warn("Content moderation failed: Bad publicity detected for brand '{}' - '{}'", 
                                        brand, content.substring(Math.max(0, brandIndex - 20), 
                                                Math.min(content.length(), brandIndex + 30)));
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Runs all content moderation checks
     * @param content The text content to check
     * @return true if content passes all moderation checks, false otherwise
     */
    public boolean moderateContent(String content) {
        boolean passesBadWords = checkForBadWords(content);
        boolean passesIllegalContent = checkForIllegalContent(content);  
        boolean passesBadPublicity = checkForBadPublicity(content);
        
        log.info("Content moderation results - Bad words: {}, Illegal content: {}, Bad publicity: {}", 
                passesBadWords, passesIllegalContent, passesBadPublicity);
        
        return passesBadWords && passesIllegalContent && passesBadPublicity;
    }
} 