package week1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Question2 {

    private final Map<String, AtomicInteger> stock = new ConcurrentHashMap<>();
    private final Map<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();

    public void addProduct(String productId, int initialStock) {
        stock.put(productId, new AtomicInteger(initialStock));
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    public String checkStock(String productId) {
        AtomicInteger count = stock.get(productId);
        if (count != null) {
            return count.get() + " units available";
        }
        return "Product not found";
    }

    public String purchaseItem(String productId, int userId) {
        AtomicInteger count = stock.get(productId);

        if (count == null) {
            return "Product not found";
        }

        while (true) {
            int currentStock = count.get();

            if (currentStock <= 0) {
                Queue<Integer> queue = waitingList.get(productId);
                queue.offer(userId);
                return "Added to waiting list, position #" + queue.size();
            }

            if (count.compareAndSet(currentStock, currentStock - 1)) {
                return "Success, " + (currentStock - 1) + " units remaining";
            }
        }
    }

    public static void main(String[] args) {
        Question2 inventory = new Question2();
        inventory.addProduct("IPHONE15_256GB", 100);

        System.out.println("checkStock(\"IPHONE15_256GB\") -> " + inventory.checkStock("IPHONE15_256GB"));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=12345) -> " + inventory.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=67890) -> " + inventory.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 98; i++) {
            inventory.purchaseItem("IPHONE15_256GB", 1000 + i);
        }

        System.out.println("... (after 100 purchases)");
        System.out.println("purchaseItem(\"IPHONE15_256GB\", userId=99999) -> " + inventory.purchaseItem("IPHONE15_256GB", 99999));
    }
}