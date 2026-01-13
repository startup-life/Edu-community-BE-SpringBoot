package kr.adapterz.edu_community.domain.user.dto.response;

import kr.adapterz.edu_community.domain.file.entity.File;
import kr.adapterz.edu_community.domain.user.entity.User;
import kr.adapterz.edu_community.global.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;

    /**
     * from: 변환 (이거로부터 만든다) | 매개변수 1개 | (낱개 데이터가 아닌, 다른 객체로부터 변환할 때) 주로 사용
     * of: 생성 (이것들로 만든다) | 매개변수 여러 개 | (낱개 데이터들을 모아서 객체 생성할 때) 주로 사용
     */

    public static UserInfoResponse of(
            Long userId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
        return new UserInfoResponse(
                userId, email, nickname, profileImageUrl
        );
    }

    public static UserInfoResponse from(User user) {
        String fullProfileUrl = Optional.ofNullable(user.getProfileImage())
                .map(File::getFilePath)
                .map(FileUtil::toFullUrl)
                .orElse(null);

        return of(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                fullProfileUrl
        );
    }
}
