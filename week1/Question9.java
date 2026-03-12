package week1;

import java.util.*;

public class Question9 {

    public static class Transaction {
        int id;
        int amount;
        String merchant;
        String time;
        String account;

        public Transaction(int id, int amount, String merchant, String time, String account) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.time = time;
            this.account = account;
        }

        @Override
        public String toString() {
            return "{id:" + id + ", amount:" + amount + ", merchant:'" + merchant + "'}";
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<String> findTwoSum(int target) {
        Map<Integer, Integer> map = new HashMap<>();
        List<String> pairs = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                pairs.add("[(id:" + map.get(complement) + ", id:" + t.id + ")]");
            }
            map.put(t.amount, t.id);
        }
        return pairs;
    }

    public List<String> detectDuplicates() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> duplicates = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant;
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(t.account);
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                String[] parts = entry.getKey().split("_");
                duplicates.add("{amount:" + parts[0] + ", merchant:\"" + parts[1] + "\", accounts:" + entry.getValue() + "}");
            }
        }
        return duplicates;
    }

    public static void main(String[] args) {
        Question9 system = new Question9();

        system.addTransaction(new Transaction(1, 500, "Store A", "10:00", "acc1"));
        system.addTransaction(new Transaction(2, 300, "Store B", "10:15", "acc2"));
        system.addTransaction(new Transaction(3, 200, "Store C", "10:30", "acc3"));
        system.addTransaction(new Transaction(4, 500, "Store A", "10:45", "acc2"));

        System.out.println("findTwoSum(500) -> " + system.findTwoSum(500));
        System.out.println("detectDuplicates() -> " + system.detectDuplicates());
    }
}