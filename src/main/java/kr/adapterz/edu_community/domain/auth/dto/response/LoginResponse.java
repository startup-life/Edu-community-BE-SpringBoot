package kr.adapterz.edu_community.domain.auth.dto.response;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private UserInfo user;
    private TokenInfo token;

    public static LoginResponse of(
            User user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponse(
                UserInfo.of(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname()
                ),
                new TokenInfo(accessToken, expiresIn)
        );
    }
}