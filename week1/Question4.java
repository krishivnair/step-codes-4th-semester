package week1;

import java.util.*;

public class Question4 {

    private static final int N_GRAM_SIZE = 5;

    private final Map<String, Set<String>> ngramIndex = new HashMap<>();
    private final Map<String, Integer> documentNgramCounts = new HashMap<>();

    public void addDocument(String docId, String content) {
        List<String> ngrams = extractNGrams(content);
        documentNgramCounts.put(docId, ngrams.size());

        for (String ngram : ngrams) {
            ngramIndex.computeIfAbsent(ngram, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String newDocId, String content) {
        List<String> ngrams = extractNGrams(content);
        int totalNgrams = ngrams.size();

        System.out.println("analyzeDocument(\"" + newDocId + "\")");
        System.out.println("-> Extracted " + totalNgrams + " n-grams");

        if (totalNgrams == 0) {
            return;
        }

        Map<String, Integer> matchCounts = new HashMap<>();

        for (String ngram : ngrams) {
            Set<String> matchedDocs = ngramIndex.get(ngram);
            if (matchedDocs != null) {
                for (String docId : matchedDocs) {
                    matchCounts.put(docId, matchCounts.getOrDefault(docId, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            String targetDocId = entry.getKey();
            int matches = entry.getValue();
            double similarity = (matches * 100.0) / totalNgrams;

            System.out.print("-> Found " + matches + " matching n-grams with \"" + targetDocId + "\"");

            if (similarity >= 50.0) {
                System.out.printf("\n-> Similarity: %.1f%% (PLAGIARISM DETECTED)\n", similarity);
            } else if (similarity >= 10.0) {
                System.out.printf("\n-> Similarity: %.1f%% (suspicious)\n", similarity);
            } else {
                System.out.printf("\n-> Similarity: %.1f%%\n", similarity);
            }
        }
    }

    private List<String> extractNGrams(String text) {
        List<String> ngrams = new ArrayList<>();
        String cleanText = text.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        String[] words = cleanText.split("\\s+");

        if (words.length < N_GRAM_SIZE) {
            return ngrams;
        }

        for (int i = 0; i <= words.length - N_GRAM_SIZE; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N_GRAM_SIZE; j++) {
                sb.append(words[i + j]);
                if (j < N_GRAM_SIZE - 1) {
                    sb.append(" ");
                }
            }
            ngrams.add(sb.toString());
        }

        return ngrams;
    }

    public static void main(String[] args) {
        Question4 detector = new Question4();

        String essay089 = "The quick brown fox jumps over the lazy dog and runs away into the deep dark forest.";
        String essay092 = "Artificial intelligence is the simulation of human intelligence processes by machines especially computer systems.";
        String essay123 = "Artificial intelligence is the simulation of human intelligence processes by machines but with a slight twist in the wording.";

        detector.addDocument("essay_089.txt", essay089);
        detector.addDocument("essay_092.txt", essay092);

        detector.analyzeDocument("essay_123.txt", essay123);
    }
}