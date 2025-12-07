package com.example.phoebe.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using Bucket4j.
 * Provides different rate limits for public and admin APIs.
 */
@Configuration
public class RateLimitConfig {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Get or create bucket for IP address with public API limits.
     * Limit: 100 requests per minute.
     */
    public Bucket getPublicBucket(String ipAddress) {
        return buckets.computeIfAbsent("public:" + ipAddress, key ->
            Bucket.builder()
                // Use the modern builder pattern for creating bandwidths
                .addLimit(Bandwidth.builder().capacity(100).refillIntervally(100, Duration.ofMinutes(1)).build())
                .build()
        );
    }

    /**
     * Get or create bucket for IP address with admin API limits.
     * Limit: 50 requests per minute (more restrictive).
     */
    public Bucket getAdminBucket(String ipAddress) {
        return buckets.computeIfAbsent("admin:" + ipAddress, key ->
            Bucket.builder()
                // Use the modern builder pattern for creating bandwidths
                .addLimit(Bandwidth.builder().capacity(50).refillIntervally(50, Duration.ofMinutes(1)).build())
                .build()
        );
    }

    /**
     * Get or create bucket for IP address with authentication limits.
     * Limit: 5 login attempts per 5 minutes (strict protection against brute-force).
     */
    public Bucket getAuthBucket(String ipAddress) {
        return buckets.computeIfAbsent("auth:" + ipAddress, key ->
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(5).refillIntervally(5, Duration.ofMinutes(5)).build())
                .build()
        );
    }
}
