package kr.adapterz.edu_community.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorInfo {

    private Long userId;
    private String nickname;
    private String profileImageUrl;

    public static AuthorInfo of(
            Long userId,
            String nickname,
            String profileImageUrl) {
        return new AuthorInfo(
                userId,
                nickname,
                profileImageUrl
        );
    }
}
