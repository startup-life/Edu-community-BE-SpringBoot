package kr.adapterz.edu_community.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "REQUIRED")
    @Size(min=2, message = "TOO_SHORT")
    @Size(max=10, message = "TOO_LONG")
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]+$",
            message = "INVALID_FORMAT"
    ) // 한글, 영문, 숫자만 가능 (특수문자, 공백 불가))
    private String nickname;

    @Pattern(
            regexp = "^.*\\.(jpg|jpeg|png|gif)$",
            message = "INVALID_FORMAT"
    ) // 이미지 확장자 체크
    private String profileImageUrl;
}
