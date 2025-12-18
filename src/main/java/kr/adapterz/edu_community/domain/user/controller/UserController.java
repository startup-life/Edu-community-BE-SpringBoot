package kr.adapterz.edu_community.domain.user.controller;

import jakarta.validation.Valid;
import kr.adapterz.edu_community.domain.user.dto.UpdateUserRequest;
import kr.adapterz.edu_community.domain.user.dto.UserInfoResponse;
import kr.adapterz.edu_community.domain.user.service.UserService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 유저 정보 가져오기
    @GetMapping("/{user_id}")
    public ApiResponse<UserInfoResponse> getUserInfo(@PathVariable("user_id") Long userId) {

        UserInfoResponse response = userService.getUserInfo(userId);
        return ApiResponse.of(
                HttpStatus.OK,
                "get_user_success",
                response
        );
    }

    // 회원 정보 수정
    @PatchMapping("/me")
    public ApiResponse<Void> updateUser(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest updateUserRequest
    ) {
        userService.updateUser(userId, updateUserRequest);

        return ApiResponse.of(
                HttpStatus.OK,
                "update_user_success",
                null
        );
    }
}
