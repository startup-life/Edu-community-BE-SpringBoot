package kr.adapterz.edu_community.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePostRequest {

    @NotBlank(message = "required")
    @Size(max = 100, message = "invalid_length")
    private String title;

    @NotBlank(message = "required")
    @Size(max = 1500, message = "invalid_length")
    private String content;

    private String attachFilePath;
}
