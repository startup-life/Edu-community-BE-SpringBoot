package kr.adapterz.edu_community.domain.auth.dto.request;

import kr.adapterz.edu_community.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {
    private Long userId;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .userId(user.getId())
                .build();
    }
}
