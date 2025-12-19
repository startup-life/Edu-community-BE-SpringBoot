package kr.adapterz.edu_community.domain.auth.dto.response;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private LoginUserResponse user;
    private TokenResponse token;

    public static LoginResponse of(
            User user,
            String accessToken,
            long expiresIn
    ) {
        return new LoginResponse(
                LoginUserResponse.from(user),
                new TokenResponse(accessToken, expiresIn)
        );
    }
}