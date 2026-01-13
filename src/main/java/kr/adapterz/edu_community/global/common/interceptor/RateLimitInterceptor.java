package kr.adapterz.edu_community.global.common.interceptor;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.edu_community.global.common.exception.RateLimitException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(100_000)
            .build();

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {
        // 1. 키 생성: IP주소 + 요청 URI 조합 (예: "192.168.0.1:/v1/auth/login")
        String key = getClientIP(request) + ":" + request.getRequestURI();

        // 2. 버킷 가져오기 (없으면 새로 생성)
        Bucket bucket = resolveBucket(key, request.getRequestURI());

        // 3. 토큰 소비 시도 (1개 사용)
        if (bucket.tryConsume(1)) {
            return true;
        }

        // [변경점] 직접 응답을 작성하지 않고 예외를 던집니다.
        // BusinessException을 상속받았으므로 GlobalExceptionHandler가 처리
        throw new RateLimitException("RATE_LIMIT_EXCEEDED");
    }

    private Bucket resolveBucket(String key, String uri) {
        // 캐시에서 key에 해당하는 버킷을 찾음. 없으면 createBucket(uri) 실행하여 저장 후 반환.
        return cache.get(key, k -> createBucket(uri));
    }

    private Bucket createBucket(String uri) {
        // 설정 파일에서 해당 URI에 맞는 규칙(Rule)을 가져옴
        RateLimitConfig.RateLimitRule rule = RateLimitConfig.getRule(uri);
        return Bucket.builder()
                .addLimit(rule.toBandwidth())
                .build();
    }

    private String getClientIP(HttpServletRequest request) {
        // AWS 로드밸런서(ALB)나 Nginx 같은 프록시 서버를 거치면 request.getRemoteAddr()이 프록시 서버 IP로 나오기 때문에,
        // 원본 IP가 담긴 헤더(X-Forwarded-For)를 먼저 확인하는 로직
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}