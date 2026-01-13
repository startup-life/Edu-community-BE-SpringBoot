package kr.adapterz.edu_community.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static UserInfoResponse of(
            Long userId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
        return new UserInfoResponse(
                userId, email, nickname, profileImageUrl
        );
    }
}
