package kr.adapterz.edu_community.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.edu_community.domain.auth.dto.*;
import kr.adapterz.edu_community.domain.auth.service.AuthService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest signupRequest
    ) {
        SignupResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(
                ApiResponse.of(200, "register_success", response)
        );
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {
        // 로그인 처리
        LoginResult result = authService.login(loginRequest);

        // Refresh Token을 HttpOnly 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        // 쿠키를 응답 헤더에 추가
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ApiResponse.of(
                200,
                "login_success",
                result.getResponse()
        );
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Auth Controller is working!";
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ApiResponse<TokenResponse> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse httpResponse
    ) {
        TokenResult result = authService.refreshAccessToken(refreshToken);

        // Refresh Token 회전 시 새 쿠키 세팅
        if (result.getNewRefreshToken() != null) {
            ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getNewRefreshToken())
                    .httpOnly(true)
                    .secure(false) // dev
                    .path("/")
                    .maxAge(14 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();
            httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ApiResponse.of(
                200,
                "token_refreshed",
                result.getToken()
        );
    }

    // 중복 이메일 검사
    @GetMapping("/email/availability")
    public ResponseEntity<ApiResponse<Void>> checkEmailAvailability(
        @RequestParam(value="email") String email
    ) {
        System.out.println(email);
        authService.validateDuplicateEmail(email);
        return ResponseEntity.ok(
            ApiResponse.of(200, "available_email", null)
        );
    }

    // 중복 닉네임 검사
    @GetMapping("/nickname/availability")
    public ResponseEntity<ApiResponse<Void>> checkNicknameAvailability(
        @RequestParam(value="nickname") String nickname
    ) {
        authService.validateDuplicateNickname(nickname);
        return ResponseEntity.ok(
            ApiResponse.of(200, "available_nickname", null)
        );
    }
}
