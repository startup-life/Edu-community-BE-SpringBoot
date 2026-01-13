package kr.adapterz.edu_community.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.adapterz.edu_community.domain.auth.dto.internal.LoginResult;
import kr.adapterz.edu_community.domain.auth.dto.internal.TokenResult;
import kr.adapterz.edu_community.domain.auth.dto.request.ChangePasswordRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.LoginRequest;
import kr.adapterz.edu_community.domain.auth.dto.request.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.response.AuthStatusResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.LoginResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.SignupResponse;
import kr.adapterz.edu_community.domain.auth.dto.response.TokenInfo;
import kr.adapterz.edu_community.domain.auth.service.AuthService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Validated
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
            @RequestParam(value="email")
            @NotBlank(message="REQUIRED")
            @Email(message="INVALID_FORMAT")
            String email
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
        @RequestParam(value="nickname")
        @NotBlank(message="REQUIRED")
        @Size(min=2, message="TOO_SHORT")
        @Size(max=10, message="TOO_LONG")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]+$",
                message = "INVALID_FORMAT"
        ) // 한글, 영문, 숫자만 가능
        String nickname
    ) {
        authService.validateDuplicateNickname(nickname);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("NICKNAME_AVAILABLE", null));
    }
}
