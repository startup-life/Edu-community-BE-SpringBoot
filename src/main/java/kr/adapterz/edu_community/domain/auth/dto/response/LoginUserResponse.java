package kr.adapterz.edu_community.domain.auth.dto.response;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUserResponse {

    private Long id;
    private String email;
    private String nickname;

    public static LoginUserResponse from(User user) {
        return new LoginUserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}