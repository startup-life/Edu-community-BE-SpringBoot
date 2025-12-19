package kr.adapterz.edu_community.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "required")
    private String nickname;
    private String profileImagePath;
}
