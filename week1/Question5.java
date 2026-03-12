package week1;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Question5 {

    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();
    private final AtomicInteger totalTraffic = new AtomicInteger(0);

    public void processEvent(String url, String userId, String source) {
        pageViews.merge(url, 1, Integer::sum);
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);
        trafficSources.merge(source.toLowerCase(), 1, Integer::sum);
        totalTraffic.incrementAndGet();
    }

    public void getDashboard() {
        System.out.println("Top Pages:");

        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue().compareTo(a.getValue())
        );

        maxHeap.addAll(pageViews.entrySet());

        int rank = 1;
        while (!maxHeap.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = maxHeap.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            System.out.printf("%d. %s - %,d views (%,d unique)\n", rank, url, views, unique);
            rank++;
        }

        System.out.println("\nTraffic Sources:");
        List<String> sourceStats = new ArrayList<>();
        int currentTotal = totalTraffic.get();

        if (currentTotal > 0) {
            List<Map.Entry<String, Integer>> sortedSources = new ArrayList<>(trafficSources.entrySet());
            sortedSources.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            for (Map.Entry<String, Integer> entry : sortedSources) {
                String source = entry.getKey();
                String capitalizedSource = source.substring(0, 1).toUpperCase() + source.substring(1);
                int percentage = (int) Math.round((entry.getValue() * 100.0) / currentTotal);
                sourceStats.add(capitalizedSource + ": " + percentage + "%");
            }
        }

        System.out.println(String.join(", ", sourceStats));
    }

    public static void main(String[] args) {
        Question5 dashboard = new Question5();

        for (int i = 0; i < 45; i++) dashboard.processEvent("/article/breaking-news", "user_" + i, "google");
        for (int i = 0; i < 30; i++) dashboard.processEvent("/article/breaking-news", "user_" + (i % 15), "direct");
        for (int i = 0; i < 15; i++) dashboard.processEvent("/sports/championship", "user_" + i, "facebook");
        for (int i = 0; i < 10; i++) dashboard.processEvent("/sports/championship", "user_" + (i % 5), "other");

        dashboard.getDashboard();
    }
}