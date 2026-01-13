package kr.adapterz.edu_community.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthStatusResponse {

    private String userId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public static AuthStatusResponse of(
            String userId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
        return new AuthStatusResponse(
                userId,
                email,
                nickname,
                profileImageUrl
        );
    }
}
