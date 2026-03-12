package week1;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Question6 {

    private final long maxTokens;
    private final long refillIntervalMs;
    private final ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    public Question6(long maxTokens, long refillIntervalMs) {
        this.maxTokens = maxTokens;
        this.refillIntervalMs = refillIntervalMs;
    }

    private class TokenBucket {
        AtomicLong tokens;
        AtomicLong lastRefillTime;

        TokenBucket() {
            this.tokens = new AtomicLong(maxTokens);
            this.lastRefillTime = new AtomicLong(System.currentTimeMillis());
        }
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket());

        synchronized (bucket) {
            long now = System.currentTimeMillis();
            long timePassed = now - bucket.lastRefillTime.get();

            if (timePassed >= refillIntervalMs) {
                bucket.tokens.set(maxTokens);
                bucket.lastRefillTime.set(now);
            }

            long currentTokens = bucket.tokens.get();
            if (currentTokens > 0) {
                bucket.tokens.decrementAndGet();
                return "Allowed (" + (currentTokens - 1) + " requests remaining)";
            } else {
                long timeUntilReset = (refillIntervalMs - timePassed) / 1000;
                return "Denied (0 requests remaining, retry after " + timeUntilReset + "s)";
            }
        }
    }

    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        if (bucket == null) return "Client not found";

        long used = maxTokens - bucket.tokens.get();
        long resetTime = bucket.lastRefillTime.get() + refillIntervalMs;

        return "{used: " + used + ", limit: " + maxTokens + ", reset: " + resetTime + "}";
    }

    public static void main(String[] args) throws InterruptedException {
        Question6 limiter = new Question6(1000, 3600000);

        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}