package kr.adapterz.edu_community.domain.auth.dto.internal;

import kr.adapterz.edu_community.domain.auth.dto.response.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {

    private LoginResponse response; // 응답 바디용
    private String refreshToken;    // 쿠키용
}
