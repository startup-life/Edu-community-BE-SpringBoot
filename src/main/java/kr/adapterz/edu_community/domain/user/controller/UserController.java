package kr.adapterz.edu_community.domain.user.controller;

import kr.adapterz.edu_community.domain.user.dto.UserInfoResponse;
import kr.adapterz.edu_community.domain.user.service.UserService;
import kr.adapterz.edu_community.global.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
