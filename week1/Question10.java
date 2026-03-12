package week1;

import java.util.*;

public class Question10 {

    private final int L1_CAPACITY = 10000;
    private final int L2_CAPACITY = 100000;
    private final int PROMOTION_THRESHOLD = 1;

    private final Map<String, String> l1Cache = new LinkedHashMap<String, String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    private final Map<String, String> l2Cache = new LinkedHashMap<String, String>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > L2_CAPACITY;
        }
    };

    private final Map<String, Integer> accessCounts = new HashMap<>();

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;
    private double totalTimeMs = 0;

    public void getVideo(String videoId) {
        System.out.println("getVideo(\"" + videoId + "\")");
        double requestTime = 0.5;

        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            System.out.println("-> L1 Cache HIT (0.5ms)");
        } else {
            System.out.println("-> L1 Cache MISS (0.5ms)");
            requestTime += 4.5;

            if (l2Cache.containsKey(videoId)) {
                l2Hits++;
                System.out.println("-> L2 Cache HIT (5ms)");

                int count = accessCounts.getOrDefault(videoId, 0) + 1;
                accessCounts.put(videoId, count);

                if (count > PROMOTION_THRESHOLD) {
                    l1Cache.put(videoId, l2Cache.get(videoId));
                    System.out.println("-> Promoted to L1");
                }
            } else {
                System.out.println("-> L2 Cache MISS");
                requestTime += 145.0;
                l3Hits++;
                System.out.println("-> L3 Database HIT (150ms)");

                int count = accessCounts.getOrDefault(videoId, 0) + 1;
                accessCounts.put(videoId, count);
                l2Cache.put(videoId, "VideoData");
                System.out.println("-> Added to L2 (access count: " + count + ")");
            }
        }

        totalTimeMs += requestTime;
        System.out.println("-> Total: " + requestTime + "ms\n");
    }

    public void getStatistics() {
        int totalRequests = l1Hits + l2Hits + l3Hits;
        if (totalRequests == 0) return;

        double l1Rate = (l1Hits * 100.0) / totalRequests;
        double l2Rate = (l2Hits * 100.0) / totalRequests;
        double l3Rate = (l3Hits * 100.0) / totalRequests;
        double avgTime = totalTimeMs / totalRequests;

        System.out.println("getStatistics() ->");
        System.out.printf("L1: Hit Rate %.0f%%, Avg Time: 0.5ms\n", l1Rate);
        System.out.printf("L2: Hit Rate %.0f%%, Avg Time: 5.0ms\n", l2Rate);
        System.out.printf("L3: Hit Rate %.0f%%, Avg Time: 150.0ms\n", l3Rate);
        System.out.printf("Overall: Hit Rate %.0f%%, Avg Time: %.1fms\n", (l1Rate + l2Rate), avgTime);
    }

    public static void main(String[] args) {
        Question10 cacheSystem = new Question10();

        cacheSystem.getVideo("video_123");
        cacheSystem.getVideo("video_123");
        cacheSystem.getVideo("video_999");

        cacheSystem.getStatistics();
    }
}