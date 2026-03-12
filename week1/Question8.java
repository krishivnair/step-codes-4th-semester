package week1;

import java.util.*;

public class Question8 {

    private final String[] spots;
    private final Map<String, Long> entryTimes;
    private int occupiedSpots;

    public Question8(int capacity) {
        this.spots = new String[capacity];
        this.entryTimes = new HashMap<>();
        this.occupiedSpots = 0;
    }

    private int hash(String licensePlate) {
        int hash = 0;
        for (char c : licensePlate.toCharArray()) {
            hash = (31 * hash + c) % spots.length;
        }
        return Math.abs(hash);
    }

    public String parkVehicle(String licensePlate) {
        if (occupiedSpots == spots.length) {
            return "Parking Lot Full";
        }

        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;
        StringBuilder log = new StringBuilder("Assigned spot #" + preferredSpot);

        while (spots[currentSpot] != null) {
            log.append("... occupied... ");
            currentSpot = (currentSpot + 1) % spots.length;
            probes++;
            if (probes > 0 && currentSpot == preferredSpot) {
                break;
            }
        }

        spots[currentSpot] = licensePlate;
        entryTimes.put(licensePlate, System.currentTimeMillis());
        occupiedSpots++;

        if (probes > 0) {
            log.append("Spot #").append(currentSpot).append(" (").append(probes).append(" probes)");
        } else {
            log.append(" (0 probes)");
        }

        return log.toString();
    }

    public String exitVehicle(String licensePlate) {
        int startSpot = hash(licensePlate);
        int currentSpot = startSpot;

        while (spots[currentSpot] != null) {
            if (spots[currentSpot].equals(licensePlate)) {
                spots[currentSpot] = null;
                long entryTime = entryTimes.remove(licensePlate);
                long durationMs = System.currentTimeMillis() - entryTime;

                double hours = Math.max(1, durationMs / 3600000.0);
                double fee = hours * 5.0;

                occupiedSpots--;
                return String.format("Spot #%d freed, Fee: $%.2f", currentSpot, fee);
            }
            currentSpot = (currentSpot + 1) % spots.length;
            if (currentSpot == startSpot) break;
        }

        return "Vehicle not found";
    }

    public String getStatistics() {
        double occupancy = (occupiedSpots * 100.0) / spots.length;
        return String.format("Occupancy: %.1f%%", occupancy);
    }

    public static void main(String[] args) {
        Question8 parkingLot = new Question8(500);

        System.out.println(parkingLot.parkVehicle("ABC-1234"));
        System.out.println(parkingLot.parkVehicle("ABC-1235"));
        System.out.println(parkingLot.parkVehicle("XYZ-9999"));

        System.out.println(parkingLot.exitVehicle("ABC-1234"));
        System.out.println(parkingLot.getStatistics());
    }
}