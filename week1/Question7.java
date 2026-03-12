package week1;
import java.util.*;

public class Question7 {

    private class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> queryFrequencies = new HashMap<>();
    }

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> globalFrequencies = new HashMap<>();

    public void updateFrequency(String query) {
        int newFreq = globalFrequencies.getOrDefault(query, 0) + 1;
        globalFrequencies.put(query, newFreq);

        TrieNode current = root;
        for (char c : query.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
            current.queryFrequencies.put(query, newFreq);
        }
        System.out.println("updateFrequency(\"" + query + "\") -> Frequency: " + newFreq);
    }

    public List<String> search(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return Collections.emptyList();
            }
            current = current.children.get(c);
        }

        PriorityQueue<Map.Entry<String, Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> a.getValue().compareTo(b.getValue())
        );

        for (Map.Entry<String, Integer> entry : current.queryFrequencies.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<String> results = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            Map.Entry<String, Integer> entry = minHeap.poll();
            results.add(entry.getKey() + " (" + entry.getValue() + " searches)");
        }
        Collections.reverse(results);
        return results;
    }

    public static void main(String[] args) {
        Question7 autocomplete = new Question7();

        autocomplete.updateFrequency("javascript");
        autocomplete.updateFrequency("javascript tutorial");
        autocomplete.updateFrequency("java download");
        for(int i=0; i<5; i++) autocomplete.updateFrequency("java tutorial");

        System.out.println("\nsearch(\"java\") ->");
        List<String> suggestions = autocomplete.search("java");
        for (int i = 0; i < suggestions.size(); i++) {
            System.out.println((i + 1) + ". \"" + suggestions.get(i) + "\"");
        }
    }
}