package kr.adapterz.edu_community.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "required")
    @Email
    private String email;

    @NotBlank(message = "required")
    private String password;

    @NotBlank(message = "required")
    private String nickname;
}
