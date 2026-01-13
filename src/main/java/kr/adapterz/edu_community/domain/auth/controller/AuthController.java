package kr.adapterz.edu_community.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.edu_community.domain.auth.dto.internal.LoginResult;
import kr.adapterz.edu_community.domain.auth.dto.internal.TokenResult;
import kr.adapterz.edu_community.domain.auth.dto.request.ChangePasswordRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.LoginRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.response.SignupResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.*;
import kr.adapterz.edu_community.domain.auth.service.AuthService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        SignupResponse response = authService.signup(signupRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of("SIGNUP_SUCCESS", response));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {
        // 로그인 처리
        LoginResult result = authService.login(loginRequest);

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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("LOGIN_SUCCESS", result.getResponse()));
    }

    // 액세스 토큰 재발급
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenInfo>> refreshAccessToken(
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

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("TOKEN_REFRESH_SUCCESS", result.getToken()));
    }

    // 로그인 상태 검증
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthStatusResponse>> checkAuthStatus(
            @AuthenticationPrincipal Long userId
    ) {
        AuthStatusResponse response = authService.checkAuthStatus(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("AUTH_CHECK_SUCCESS", response));
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
    ) {
        authService.changePassword(userId, changePasswordRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_PASSWORD_UPDATED", null));
    }

    // 중복 이메일 검사
    @GetMapping("/email/availability")
    public ResponseEntity<ApiResponse<Void>> checkEmailAvailability(
        @RequestParam(value="email") String email
    ) {
        System.out.println(email);
        authService.validateDuplicateEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("EMAIL_AVAILABLE", null));
    }

    // 중복 닉네임 검사
    @GetMapping("/nickname/availability")
    public ResponseEntity<ApiResponse<Void>> checkNicknameAvailability(
        @RequestParam(value="nickname") String nickname
    ) {
        authService.validateDuplicateNickname(nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("NICKNAME_AVAILABLE", null));
    }
}
