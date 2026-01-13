package kr.adapterz.edu_community.global.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
                /*
                jwtProperties.getSecret(): application.yml에 설정한 값, 타입: String
                .getBytes(StandardCharsets.UTF_8): 문자열을 UTF-8 인코딩된 바이트 배열(byte[]) 로 변환, 결과 타입: byte[]

                왜 하느냐
                -> JWT 라이브러리(jjwt)의 요구사항 때문
                    1. HMAC-SHA 알고리즘은 대칭키 암호화 방식
                    2. jjwt 라이브러리는 대칭키를 byte[] 형태로 받음
                    3. 따라서, 문자열(secret)을 byte[]로 변환해야 함
                 즉, "문자열 secret → 바이트 → 암호 키" <- 이 과정을 명시적으로 해주는 것
                 */
        );
    }

    public String createAccessToken(Long userId, String email, String nickname) {
        return createToken(
                "access",
                userId,
                Map.of("email", email, "nickname", nickname),
                jwtProperties.getAccessTokenExpSeconds()
        );
    }

    public String createRefreshToken(Long userId) {
        return createToken(
                "refresh",
                userId,
                Map.of(),
                jwtProperties.getRefreshTokenExpSeconds()
        );
    }

    private String createToken(
            String type,
            Long userId,
            Map<String, Object> claims,
            long expSeconds
    ) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", type)
                .claims(claims)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expSeconds)))
                .signWith((SecretKey) key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(parse(token).getPayload().get("typ", String.class));
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getPayload().getSubject());
    }

    public Long getAccessTokenValidityInMilliseconds() {
        return jwtProperties.getAccessTokenExpSeconds() * 1000;
    }
}
