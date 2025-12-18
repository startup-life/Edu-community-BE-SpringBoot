package kr.adapterz.edu_community.domain.user.dto;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImagePath;
    private LocalDateTime createdAt;

    public static UserInfoResponse of(User user, String profileImagePath) {
        return new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                profileImagePath,
                user.getCreatedAt()
        );
    }
}
