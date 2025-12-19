package kr.adapterz.edu_community.domain.auth.dto.response;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String email;
    private String nickname;

    public static UserInfo from(User user) {
        return new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}