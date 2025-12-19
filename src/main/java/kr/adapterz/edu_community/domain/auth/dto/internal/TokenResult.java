package kr.adapterz.edu_community.domain.auth.dto.internal;

import kr.adapterz.edu_community.domain.auth.dto.response.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResult {
    private TokenResponse token;        // 응답 바디 (accessToken, expiresIn)
    private String newRefreshToken;     // 회전 시에만 사용 (없으면 null)
}