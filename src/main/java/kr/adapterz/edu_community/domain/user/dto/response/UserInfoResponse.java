package kr.adapterz.edu_community.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImagePath;
    private LocalDateTime createdAt;

    public static UserInfoResponse of(
            Long userId,
            String email,
            String nickname,
            String profileImagePath,
            LocalDateTime createdAt
    ) {
        return new UserInfoResponse(
                userId, email, nickname, profileImagePath, createdAt
        );
    }
}
