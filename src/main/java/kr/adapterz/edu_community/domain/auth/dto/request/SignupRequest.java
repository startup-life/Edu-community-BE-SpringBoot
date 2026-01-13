package kr.adapterz.edu_community.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "REQUIRED")
    @Email(message = "INVALID_FORMAT")
    private String email;

    @NotBlank(message = "REQUIRED")
    @Size(min = 8, message = "TOO_SHORT") // 길이가 8 미만
    @Size(max = 20, message = "TOO_LONG") // 길이가 20 초과
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "INVALID_FORMAT"
    ) // 영문, 숫자, 특수문자를 최소 1개씩 포함해야 함
    private String password;

    @NotBlank(message = "REQUIRED")
    @Size(min = 2, message = "TOO_SHORT") // 명세에는 없지만 보통 최소길이 존재
    @Size(max = 10, message = "TOO_LONG")
    @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]+$",
            message = "INVALID_FORMAT"
    ) // 한글, 영문, 숫자만 가능 (특수문자, 공백 불가)
    private String nickname;

    @Pattern(
            regexp = "^.*\\.(jpg|jpeg|png|gif)$",
            message = "INVALID_FORMAT"
    ) // 이미지 확장자 체크
    private String profileImageUrl;
}
