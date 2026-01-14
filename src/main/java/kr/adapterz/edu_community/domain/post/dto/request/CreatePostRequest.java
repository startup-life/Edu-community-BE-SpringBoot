package kr.adapterz.edu_community.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatePostRequest {

    @NotBlank(message = "REQUIRED")
    @Size(max = 100, message = "TOO_LONG")
    private String title;

    @NotBlank(message = "REQUIRED")
    @Size(max = 1500, message = "TOO_LONG")
    private String content;

    @Pattern(
            regexp = "^.*\\.(jpg|jpeg|png|gif)$",
            message = "INVALID_FORMAT"
    ) // 이미지 확장자 체크
    private String attachFileUrl;
}
