package kr.adapterz.edu_community.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "required")
    private String password;
}