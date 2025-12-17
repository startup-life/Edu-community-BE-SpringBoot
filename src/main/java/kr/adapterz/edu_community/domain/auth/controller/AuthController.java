package kr.adapterz.edu_community.domain.auth.controller;

import jakarta.validation.Valid;
import kr.adapterz.edu_community.domain.auth.dto.SignupRequest;
import kr.adapterz.edu_community.domain.auth.dto.SignupResponse;
import kr.adapterz.edu_community.domain.auth.service.AuthService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
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
