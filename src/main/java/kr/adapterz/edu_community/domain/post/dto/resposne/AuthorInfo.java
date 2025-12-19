package kr.adapterz.edu_community.domain.post.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorDto {

    private Long userId;
    private String nickname;
    private String profileImagePath;

    public static AuthorDto of(Long userId, String nickname, String profileImagePath) {
        return new AuthorDto(userId, nickname, profileImagePath);
    }
}
