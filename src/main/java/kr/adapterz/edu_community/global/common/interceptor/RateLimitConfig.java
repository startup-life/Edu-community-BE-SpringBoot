package kr.adapterz.edu_community.global.common.interceptor;

import io.github.bucket4j.Bandwidth;
import lombok.Getter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class RateLimitConfig {

    // Spring에서 URL 패턴 매칭에 사용하는 유틸리티
    private static final PathMatcher pathMatcher = new AntPathMatcher();

    // 순서가 보장되는 Map 사용
    private static final Map<String, RateLimitRule> RULES = new LinkedHashMap<>();

    static {
        RULES.put("/v1/auth/signup", new RateLimitRule(3, Duration.ofSeconds(10)));
        RULES.put("/v1/auth/login", new RateLimitRule(5, Duration.ofMinutes(1)));

        // ** (와일드카드) 예시 추가
        // /v1/posts등 하위 경로 모두 포함
        RULES.put("/v1/posts/**", new RateLimitRule(100, Duration.ofMinutes(1)));

        // 그 외 나머지는 default (맨 마지막에 매칭되지 않으면 처리되므로 Map에 안 넣어도 됨)
    }

    public static RateLimitRule getRule(String uri) {
        // Map을 순회하면서 패턴 매칭 시도
        for (Map.Entry<String, RateLimitRule> entry : RULES.entrySet()) {
            if (pathMatcher.match(entry.getKey(), uri)) {
                return entry.getValue();
            }
        }
        // 매칭되는 게 없으면 기본값 반환
        return new RateLimitRule(10, Duration.ofMinutes(1));
    }

    public record RateLimitRule(int capacity, Duration duration) {
        public Bandwidth toBandwidth() {
            return Bandwidth.builder()
                    .capacity(capacity)
                    .refillGreedy(capacity, duration)
                    .build();
        }
    }
}