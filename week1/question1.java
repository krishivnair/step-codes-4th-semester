import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class question1 {
    // Use ConcurrentHashMap to handle 1000 concurrent requests/sec safely
    private static Map<String, Integer> userRegistry = new ConcurrentHashMap<>();
    private static Map<String, Integer> attemptTracker = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Pre-populating some "taken" users
        userRegistry.put("john_doe", 101);
        userRegistry.put("jane_smith", 102);
        userRegistry.put("admin", 103);

        System.out.println("Availability Checks");
        testCheck("john_doe");   // Taken
        testCheck("jane_smith"); // Taken
        testCheck("lucky_star"); // Available

        System.out.println("\nSuggestions for 'john_doe'");
        System.out.println(suggestAlternatives("john_doe"));

        // Simulating multiple attempts
        simulateAttempts("admin", 10543);
        simulateAttempts("guest", 500);
        simulateAttempts("john_doe", 1200);

        System.out.println("\nAnalytics");
        System.out.println("Most Attempted: " + getMostAttempted());
    }

    public static boolean checkAvailability(String username) {
        // Use merge or compute for thread-safe atomic increments
        attemptTracker.merge(username, 1, Integer::sum);

        // O(1) Lookup (removed .toLowerCase() for consistency)
        return !userRegistry.containsKey(username);
    }

    public static List<String> suggestAlternatives(String username) {
        List<String> alternatives = new ArrayList<>();
        int suffix = 1;

        while (alternatives.size() < 3) {
            String candidate = username + suffix;
            if (!userRegistry.containsKey(candidate)) {
                alternatives.add(candidate);
            }
            suffix++;
        }

        // To strictly match the prompt's sample output, we can add a modified character version
        String dotVersion = username.replace("_", ".");
        if (!userRegistry.containsKey(dotVersion) && !alternatives.contains(dotVersion)) {
            alternatives.set(2, dotVersion); // Replace the last number with the dot version
        }

        return alternatives;
    }

    public static String getMostAttempted() {
        if (attemptTracker.isEmpty()) return "None";

        PriorityQueue<Map.Entry<String, Integer>> maxHeap =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        maxHeap.addAll(attemptTracker.entrySet());
        Map.Entry<String, Integer> top = maxHeap.poll();

        return top.getKey() + " (" + top.getValue() + " attempts)";
    }

    private static void testCheck(String name) {
        System.out.println(name + " available? " + checkAvailability(name));
    }

    private static void simulateAttempts(String name, int count) {
        attemptTracker.merge(name, count, Integer::sum);
    }
}