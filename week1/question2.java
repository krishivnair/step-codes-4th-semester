package week1;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class question2 {
    private static Map<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    private static Map<String, ConcurrentLinkedQueue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        String productId = "IPHONE15_256GB";

        inventory.put(productId, new AtomicInteger(100));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());

        System.out.println("--- Initial Status ---");
        System.out.println("checkStock(\"" + productId + "\") -> " + checkStock(productId));

        System.out.println("\n--- Processing Purchases ---");
        System.out.println(purchaseItem(productId, 12345));
        System.out.println(purchaseItem(productId, 67890));

        System.out.println("\n--- Simulating High Concurrency Flash Sale (150 users) ---");
        simulateFlashSaleRush(productId, 150);

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // 5. Final Status
        System.out.println("\n--- Final Status ---");
        System.out.println("Final Stock: " + checkStock(productId));
        System.out.println("People on Waiting List: " + waitingLists.get(productId).size());

        // Peek at the first person in the waitlist
        System.out.println("Next in line: User " + waitingLists.get(productId).peek());
    }

    public static String checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        if (stock == null) return "Product not found";

        return stock.get() + " units available";
    }

    public static String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) {
            return "Product not found";
        }

        while (true) {
            int currentStock = stock.get();

            if (currentStock <= 0) {
                ConcurrentLinkedQueue<Integer> waitlist = waitingLists.get(productId);
                waitlist.add(userId);
                return "purchaseItem(\"" + productId + "\", userId=" + userId +
                        ") -> Added to waiting list, position #" + waitlist.size();
            }

            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "purchaseItem(\"" + productId + "\", userId=" + userId +
                        ") -> Success, " + (currentStock - 1) + " units remaining";
            }
        }
    }

    private static void simulateFlashSaleRush(String productId, int numberOfUsers) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfUsers);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < numberOfUsers; i++) {
            final int userId = 90000 + i;
            executor.submit(() -> {
                try {
                    latch.await();
                    String result = purchaseItem(productId, userId);
                    if (result.contains("waiting list") && userId % 10 == 0) {
                        System.out.println(result);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        latch.countDown();
        executor.shutdown();
    }
}