package kr.adapterz.edu_community.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private Long userId;

    public static SignupResponse of(Long userId) {
        return new SignupResponse(
                userId
        );
    }
}
