package kr.adapterz.edu_community.domain.post.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorResult {

    private Long userId;
    private String nickname;
    private String profileImagePath;

    public static AuthorResult of(Long userId, String nickname, String profileImagePath) {
        return new AuthorResult(userId, nickname, profileImagePath);
    }
}
