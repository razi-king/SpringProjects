package com.example.practice.config;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RateLimitConfig {
	private final Map<String,Bucket> cache = new ConcurrentHashMap<>();
	@Bean
	public Bucket resolveBucket(HttpServletRequest request) {
		String ip = request.getRemoteAddr();
		return cache.computeIfAbsent(ip, k->Bucket.builder().addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))).build());
	}
}
