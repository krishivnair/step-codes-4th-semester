package week1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Question1 {

    private final Map<String, String> usernames = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();

    public void registerUser(String username, String userId) {
        usernames.put(username, userId);
    }

    public boolean checkAvailability(String username) {
        attempts.computeIfAbsent(username, k -> new AtomicInteger(0)).incrementAndGet();
        return !usernames.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int count = 1;

        while (suggestions.size() < 3) {
            String alt1 = username + count;
            if (!usernames.containsKey(alt1)) {
                suggestions.add(alt1);
            }

            if (suggestions.size() < 3) {
                String alt2 = username.replace("_", ".") + count;
                if (!usernames.containsKey(alt2) && !suggestions.contains(alt2)) {
                    suggestions.add(alt2);
                }
            }
            count++;
        }
        return suggestions;
    }

    public String getMostAttempted() {
        String mostAttempted = null;
        int maxAttempts = 0;

        for (Map.Entry<String, AtomicInteger> entry : attempts.entrySet()) {
            int currentAttempts = entry.getValue().get();
            if (currentAttempts > maxAttempts) {
                maxAttempts = currentAttempts;
                mostAttempted = entry.getKey();
            }
        }
        return mostAttempted != null ? mostAttempted + " (" + maxAttempts + " attempts)" : "No attempts recorded";
    }

    public static void main(String[] args) {
        Question1 system = new Question1();

        system.registerUser("john_doe", "user_001");
        system.registerUser("admin", "user_000");

        for (int i = 0; i < 10543; i++) {
            system.checkAvailability("admin");
        }

        System.out.println("checkAvailability(\"john_doe\") -> " + system.checkAvailability("john_doe"));
        System.out.println("checkAvailability(\"jane_smith\") -> " + system.checkAvailability("jane_smith"));
        System.out.println("suggestAlternatives(\"john_doe\") -> " + system.suggestAlternatives("john_doe"));
        System.out.println("getMostAttempted() -> " + system.getMostAttempted());
    }
}