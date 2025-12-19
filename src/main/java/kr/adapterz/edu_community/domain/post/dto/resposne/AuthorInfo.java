package kr.adapterz.edu_community.domain.post.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorInfo {

    private Long userId;
    private String nickname;
    private String profileImagePath;

    public static AuthorInfo of(Long userId, String nickname, String profileImagePath) {
        return new AuthorInfo(userId, nickname, profileImagePath);
    }
}
