package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorInfo {

    private Long userId;
    private String nickname;
    private String profileImagePath;

    public static AuthorInfo of(
            Long userId,
            String nickname,
            String profileImagePath) {
        return new AuthorInfo(
                userId,
                nickname,
                profileImagePath
        );
    }

    /*public static AuthorInfo from(
            User user,
            String profileImagePath
    ) {
        return new AuthorInfo(
                user.getId(),
                user.getNickname(),
                profileImagePath
        );
    }*/
}
