package kr.adapterz.edu_community.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {

    private LoginResponse response; // 응답 바디용
    private String refreshToken;    // 쿠키용
}
