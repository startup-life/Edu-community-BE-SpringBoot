package kr.adapterz.edu_community.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String email;
    private String nickname;

    public static UserInfo of(
            Long id,
            String email,
            String nickname
    ) {
        return new UserInfo(
                id,
                email,
                nickname
        );
    }
}