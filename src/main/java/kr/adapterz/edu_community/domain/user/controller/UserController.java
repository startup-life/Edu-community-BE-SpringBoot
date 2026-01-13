package kr.adapterz.edu_community.domain.user.controller;

import jakarta.validation.Valid;
import kr.adapterz.edu_community.domain.user.dto.request.UpdateUserRequest;
import kr.adapterz.edu_community.domain.user.dto.response.UserInfoResponse;
import kr.adapterz.edu_community.domain.user.service.UserService;
import kr.adapterz.edu_community.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(
            @AuthenticationPrincipal Long userId
    ) {
        UserInfoResponse response = userService.getUserInfo(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_RETRIEVED", response));
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        userService.updateUser(userId, updateUserRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_UPDATED", null));
    }

    // 회원 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(
            @AuthenticationPrincipal Long userId
    ) {
        userService.withdrawUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of("USER_DELETED", null));
    }
}
