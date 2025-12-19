package kr.adapterz.edu_community.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private long expiresIn;
}